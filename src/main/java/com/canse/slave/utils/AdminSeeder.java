package com.canse.slave.utils;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class AdminSeeder {

    private final AdminGenerator adminGenerator;

    @PostConstruct
    public void init() {
        adminGenerator.seedRolesAndAdmin();
    }
}
