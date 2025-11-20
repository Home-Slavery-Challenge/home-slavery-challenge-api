package com.canse.domestic_task_api;

import com.canse.domestic_task_api.utils.AdminGenerator;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DomesticTaskApiApplication {


    @Autowired
    private AdminGenerator adminGenerator;


    public static void main(String[] args) {
        SpringApplication.run(DomesticTaskApiApplication.class, args);
    }


    @PostConstruct
    void init() {
        adminGenerator.seedRolesAndAdmin();
    }
}
