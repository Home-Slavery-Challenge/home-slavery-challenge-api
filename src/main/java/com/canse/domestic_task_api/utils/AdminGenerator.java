package com.canse.domestic_task_api.utils;

import com.canse.domestic_task_api.entities.Role;
import com.canse.domestic_task_api.entities.User;
import com.canse.domestic_task_api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdminGenerator {

    @Autowired
    private UserService userService;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private EnvConfig envConfig;

    public void seedRolesAndAdmin(){
        List<Role> userRole = userService.getRoles();
        if(userRole.isEmpty()){
            userService.addRole(new Role(null, "ADMIN"));
            userService.addRole(new Role(null, "USER"));
            User admin = User.builder()
                    .username(envConfig.getAdminUsername())
                    .email(envConfig.getAdminEmail())
                    .password(bCryptPasswordEncoder.encode(envConfig.getAdminPassword()))
                    .enabled(true)
                    .build();

            userService.saveUser(admin);

            userService.addRoleToUser(envConfig.getAdminUsername(), "ADMIN");
        }else {
            System.out.println("-------------------------------");
            System.out.println("| Admin and roles already exist |");
            System.out.println("-------------------------------");
        }
    }
}
