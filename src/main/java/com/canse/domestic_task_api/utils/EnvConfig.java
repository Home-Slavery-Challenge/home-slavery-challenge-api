package com.canse.domestic_task_api.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class EnvConfig {

    @Autowired
    Environment environment;

    public String getAdminUsername() {
        return environment.getProperty("ADMINUSERNAME");
    }

    public String getAdminPassword(){
        return environment.getProperty("ADMINPASSWORD");
    }

}