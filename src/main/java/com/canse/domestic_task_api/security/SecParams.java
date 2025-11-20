package com.canse.domestic_task_api.security;

public interface SecParams {
    public static final long EXP_TIME = 10 * 24 * 60 * 60 * 1000;
    public static final String SECRET = "canse";
    public static final String PREFIX = "Bearer ";
}
