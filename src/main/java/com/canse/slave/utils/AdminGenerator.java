package com.canse.slave.utils;

import com.canse.slave.entities.Role;
import com.canse.slave.entities.Users;
import com.canse.slave.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdminGenerator {

    @Autowired
    private UserService userService;

    @Autowired
    private EnvConfig envConfig;

    public void seedRolesAndAdmin() {
        List<Role> userRole = userService.getRoles();
        if (userRole.isEmpty()) {
            userService.addRole(new Role("ADMIN"));
            userService.addRole(new Role("USER"));
            Users admin = Users.builder()
                    .username(envConfig.getAdminUsername())
                    .email(envConfig.getAdminEmail())
                    .password(envConfig.getAdminPassword())
                    .enabled(true)
                    .build();

            userService.saveUser(admin);

            userService.addRoleToUser(envConfig.getAdminUsername(), "ADMIN");
        } else {
            System.out.println("-------------------------------");
            System.out.println("| Admin and roles already exist |");
            System.out.println("-------------------------------");
        }
    }
}
