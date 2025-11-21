package com.canse.slave.controllers;


import com.canse.slave.entities.RegistrationRequest;
import com.canse.slave.entities.User;
import com.canse.slave.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthRestController {
    @Autowired
    UserService userService;

    @PostMapping("/register")
    public User register(@RequestBody RegistrationRequest registrationRequest){
        return userService.registerUser(registrationRequest);
    }

    @GetMapping("/verify-email/{token}")
    public User verifyEmail(@PathVariable String token){
        return userService.validateToken(token);
    }
}