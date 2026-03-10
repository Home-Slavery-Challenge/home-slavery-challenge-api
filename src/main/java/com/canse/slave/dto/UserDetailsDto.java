package com.canse.slave.dto;

import java.time.LocalDateTime;

public record UserDetailsDto(Long id, String username, String email, LocalDateTime createdAt,LocalDateTime updatedAt, Integer nbChallenge, Integer nbFriend) {
}

