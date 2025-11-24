package com.canse.slave.services;

import com.canse.slave.entities.Friendship;
import com.canse.slave.entities.User;
import com.canse.slave.repos.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
@Service
public class FriendshipServiceImpl implements FriendshipService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> searchUsersByName(String query, String currentUser) {
//        Use DTO and get only field necessary
        return userRepository.findByUsernameContainsIgnoreCase(query).stream()
                .filter(u -> !u.getUsername().equals(currentUser))
                .toList();
    }

    @Override
    public Friendship sendFriendRequest(UserDetails currentUser, Long targetUserId) {
        return null;
    }

    @Override
    public Friendship acceptRequest(UserDetails currentUser, Long friendshipId) {
        return null;
    }

    @Override
    public Friendship declineRequest(UserDetails currentUser, Long friendshipId) {
        return null;
    }

    @Override
    public Friendship blockUser(UserDetails currentUser, Long targetUserId) {
        return null;
    }

    @Override
    public List<Friendship> getPendingReceivedRequests(UserDetails currentUser) {
        return List.of();
    }

    @Override
    public List<Friendship> getPendingSentRequests(UserDetails currentUser) {
        return List.of();
    }

    @Override
    public Friendship markAsChecked(UserDetails currentUser, Long friendshipId) {
        return null;
    }

    @Override
    public List<User> getFriends(UserDetails currentUser) {
        return List.of();
    }

    @Override
    public void removeFriend(UserDetails currentUser, Long friendId) {

    }
}
