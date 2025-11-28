package com.canse.slave;

import com.canse.slave.entities.Users;
import com.canse.slave.repos.FriendshipRepository;
import com.canse.slave.repos.UserRepository;
import com.canse.slave.services.FriendshipService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class FriendshipServiceImplTest {

    @Autowired
    private FriendshipService friendshipService;

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private UserRepository userRepository;

    // searchUsersByName
    @Test
    void searchUsersByName_shouldReturnMatchingUsers_excludingCurrentUser() {
        List<Users> result = friendshipService.searchUsersByName("a", "alice");
        assertFalse(result.isEmpty());
        assertEquals(result.size(), 1);
        assertTrue(result.stream().anyMatch(u -> u.getUsername().equals("charlie")));
    }


}
