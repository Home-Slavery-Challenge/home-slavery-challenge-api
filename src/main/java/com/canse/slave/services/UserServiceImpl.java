package com.canse.slave.services;

import com.canse.slave.entities.RegistrationRequest;
import com.canse.slave.entities.Role;
import com.canse.slave.entities.Users;
import com.canse.slave.entities.VerificationToken;
import com.canse.slave.exceptions.EmailAlreadyExistException;
import com.canse.slave.exceptions.ExpiredTokenException;
import com.canse.slave.exceptions.InvalidTokenException;
import com.canse.slave.repos.RoleRepository;
import com.canse.slave.repos.UserRepository;
import com.canse.slave.repos.VerificationTokenRepository;
import com.canse.slave.utils.EmailSender;
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
    public Users saveUser(Users user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return this.userRepository.save(user);
    }

    @Override
    public Users findUserByUsername(String username) {
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
    public Users addRoleToUser(String username, String roleName) {
        Users user = this.userRepository.findByUsername(username);
        Role role = this.roleRepository.findByName(roleName);
        user.getRoles().add(role);
        return user;
    }

    @Override
    public List<Users> findAllUsers() {
        return this.userRepository.findAll();
    }

    @Override
    public Users registerUser(RegistrationRequest request) {

        Optional<Users> user = userRepository.findByEmail(request.getEmail());

        if (user.isPresent()) {
            throw new EmailAlreadyExistException("Email deja existant !");
        }

        Users newUser = new Users();
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
    public void sendEmailUser(Users user, String code) {
        String body = "Bonjour " + "<h1>" + user.getUsername() + "</h1>" + " Votre code de validation est " + "<h1>" + code + "</h1>";
        emailSender.sendEmail(user.getEmail(), body);
    }

    @Override
    public Users validateToken(String code) {
        VerificationToken token = verifTokenRepository.findByToken(code);
        if(token == null){
            throw  new InvalidTokenException("Invalide token !");
        }
        Users user = token.getUser();
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
