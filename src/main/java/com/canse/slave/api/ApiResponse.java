package com.canse.slave.api;

import java.util.List;

public record ApiResponse<T>(
        T data,
        String message,
        List<String> changed) {}