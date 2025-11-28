package com.canse.slave.repos;

import com.canse.slave.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    Users findByUsername(String username);
    Optional<Users> findByEmail(String email);
    List<Users> findByUsernameContainsIgnoreCase(String username);
}
