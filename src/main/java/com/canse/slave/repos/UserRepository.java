package com.canse.slave.repos;

import com.canse.slave.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByUsernameContainsIgnoreCase(String username);
}
