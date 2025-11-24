package com.canse.slave.services;

import com.canse.slave.entities.Friendship;
import com.canse.slave.entities.User;
import com.canse.slave.enums.FriendshipStatus;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface FriendshipService {

    List<User> searchUsersByName(String query, String currentUser);

    Friendship sendFriendRequest(UserDetails currentUser, Long targetUserId);

    Friendship acceptRequest(UserDetails currentUser, Long friendshipId);
    Friendship declineRequest(UserDetails currentUser, Long friendshipId);
    Friendship blockUser(UserDetails currentUser, Long targetUserId);

    List<Friendship> getPendingReceivedRequests(UserDetails currentUser);
    List<Friendship> getPendingSentRequests(UserDetails currentUser);

    Friendship markAsChecked(UserDetails currentUser, Long friendshipId);

    List<User> getFriends(UserDetails currentUser);

    void removeFriend(UserDetails currentUser, Long friendId);
}
