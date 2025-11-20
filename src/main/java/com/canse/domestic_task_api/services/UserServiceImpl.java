package com.canse.domestic_task_api.services;

import com.canse.domestic_task_api.entities.RegistrationRequest;
import com.canse.domestic_task_api.entities.Role;
import com.canse.domestic_task_api.entities.User;
import com.canse.domestic_task_api.entities.VerificationToken;
import com.canse.domestic_task_api.exceptions.EmailAlreadyExistException;
import com.canse.domestic_task_api.exceptions.ExpiredTokenException;
import com.canse.domestic_task_api.exceptions.InvalidTokenException;
import com.canse.domestic_task_api.repos.RoleRepository;
import com.canse.domestic_task_api.repos.UserRepository;
import com.canse.domestic_task_api.repos.VerificationTokenRepository;
import com.canse.domestic_task_api.utils.EmailSender;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Transactional
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    VerificationTokenRepository verifTokenRepository;

    @Autowired
    EmailSender emailSender;

    @Override
    public User saveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return this.userRepository.save(user);
    }

    @Override
    public User findUserByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }

    @Override
    public Role addRole(Role role) {
        return this.roleRepository.save(role);
    }

    @Override
    public List<Role> getRoles() {
        return this.roleRepository.findAll();
    }

    @Override
    public User addRoleToUser(String username, String roleName) {
        User user = this.userRepository.findByUsername(username);
        Role role = this.roleRepository.findByName(roleName);
        user.getRoles().add(role);
        return user;
    }

    @Override
    public List<User> findAllUsers() {
        return this.userRepository.findAll();
    }

    @Override
    public User registerUser(RegistrationRequest request) {

        Optional<User> user = userRepository.findByEmail(request.getEmail());

        if (user.isPresent()) {
            throw new EmailAlreadyExistException("Email deja existant !");
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
        newUser.setEmail(request.getEmail());
        newUser.setEnabled(false);
        userRepository.save(newUser);

        Role role = roleRepository.findByName("USER");
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        newUser.setRoles(roles);

        String code = this.generateCode();
        VerificationToken token = new VerificationToken(code, newUser);
        verifTokenRepository.save(token);

        sendEmailUser(newUser, code);

        return userRepository.save(newUser);
    }

    private String generateCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return Integer.toString(code);
    }

    @Override
    public void sendEmailUser(User user, String code) {
        String body = "Bonjour " + "<h1>" + user.getUsername() + "</h1>" + " Votre code de validation est " + "<h1>" + code + "</h1>";
        emailSender.sendEmail(user.getEmail(), body);
    }

    @Override
    public User validateToken(String code) {
        VerificationToken token = verifTokenRepository.findByToken(code);
        if(token == null){
            throw  new InvalidTokenException("Invalide token !");
        }
        User user = token.getUser();
        Calendar calendar = Calendar.getInstance();
        if((token.getExpirationTime().getTime() - calendar.getTime().getTime()) <= 0){
            verifTokenRepository.delete(token);
            throw new ExpiredTokenException("Expired token !");
        }
        user.setEnabled(true);
        userRepository.save(user);
        return user;
    }

}
