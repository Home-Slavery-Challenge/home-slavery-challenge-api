package com.canse.slave.services;

import com.canse.slave.entities.RegistrationRequest;
import com.canse.slave.entities.Role;
import com.canse.slave.entities.Users;

import java.util.List;

public interface UserService {
    Users saveUser(Users user);
    Users findUserByUsername(String username);
    Role addRole(Role role);
    List<Role> getRoles();
    Users addRoleToUser(String username, String roleName);
    List<Users> findAllUsers();
    Users registerUser(RegistrationRequest request);
    public void sendEmailUser(Users user, String code);
    public Users validateToken(String code);
}
