package com.canse.slave.controllers;


import com.canse.slave.entities.RegistrationRequest;
import com.canse.slave.entities.Users;
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
    public Users register(@RequestBody RegistrationRequest registrationRequest){
        return userService.registerUser(registrationRequest);
    }

    @GetMapping("/verify-email/{token}")
    public Users verifyEmail(@PathVariable String token){
        return userService.validateToken(token);
    }
}