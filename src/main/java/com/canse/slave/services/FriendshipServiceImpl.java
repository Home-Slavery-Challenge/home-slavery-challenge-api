package com.canse.slave.services;

import com.canse.slave.entities.Friendship;
import com.canse.slave.entities.User;
import com.canse.slave.enums.FriendshipStatus;
import com.canse.slave.repos.FriendshipRepository;
import com.canse.slave.repos.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

// TODO : Creer des DTO

@Transactional
@Service
public class FriendshipServiceImpl implements FriendshipService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Override
    public List<User> searchUsersByName(String query, String currentUser) {
        return userRepository.findByUsernameContainsIgnoreCase(query).stream()
                .filter(u -> !u.getUsername().equals(currentUser))
                .toList();
    }

    @Override
    public Friendship sendFriendRequest(String currentUser, Long targetUserId) {

        User userRequester = userRepository.findByUsername(currentUser);
        User userReceiver = userRepository.findById(targetUserId).get();

        Friendship friendship = new Friendship();
        friendship.setRequester(userRequester);
        friendship.setReceiver(userReceiver);
        friendship.setChecked(false);
        friendship.setStatus(FriendshipStatus.PENDING);
        return friendshipRepository.save(friendship);
    }

    @Override
    public Friendship acceptRequest(Long friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId).get();
        friendship.setStatus(FriendshipStatus.ACCEPTED);
        friendship.setChecked(true);
        return friendshipRepository.save(friendship);
    }

    @Override
    public void declineRequest(Long friendshipId) {
        friendshipRepository.deleteById(friendshipId);
    }

    @Override
    public Friendship blockUser(Long friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId).get();
        friendship.setStatus(FriendshipStatus.BLOCKED);
        friendship.setChecked(true);
        return friendshipRepository.save(friendship);
    }

    @Override
    public List<Friendship> getPendingReceivedRequests(String currentUser) {
        return friendshipRepository.getPendingReceivedRequestsByUser(currentUser);
    }


    @Override
    public List<Friendship> getPendingSentRequests(String currentUser) {
        return friendshipRepository.getPendingSentRequests(currentUser);
    }

    @Override
    public Friendship markAsChecked(Long friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId).get();
        friendship.setChecked(true);
        return friendshipRepository.save(friendship);
    }

    @Override
    public List<User> getFriends(String currentUser) {
        return friendshipRepository.findFriendsOfUser(currentUser);
    }

    @Override
    public void removeFriend(Long friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId).get();
        friendshipRepository.deleteById(friendshipId);
    }
}
