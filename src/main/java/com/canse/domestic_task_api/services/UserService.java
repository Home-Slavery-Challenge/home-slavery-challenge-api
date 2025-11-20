package com.canse.domestic_task_api.services;

import com.canse.domestic_task_api.entities.RegistrationRequest;
import com.canse.domestic_task_api.entities.Role;
import com.canse.domestic_task_api.entities.User;

import java.util.List;

public interface UserService {
    User saveUser(User user);
    User findUserByUsername(String username);
    Role addRole(Role role);
    User addRoleToUser(String username, String roleName);
    List<User> findAllUsers();
    User registerUser(RegistrationRequest request);
    public void sendEmailUser(User user, String code);
    public User validateToken(String code);
}
