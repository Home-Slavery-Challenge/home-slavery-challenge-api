package com.canse.slave.dto;

import java.time.LocalDateTime;

public record UserDetailsDto(Long id, String username, String email, LocalDateTime createdAt, Integer nbChallenge, Integer nbFriend) {
}

