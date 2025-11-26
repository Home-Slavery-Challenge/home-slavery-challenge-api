package com.canse.slave.services;

import com.canse.slave.entities.Friendship;
import com.canse.slave.entities.User;

import java.util.List;

public interface FriendshipService {

    List<User> searchUsersByName(String query, String currentUser);

    Friendship sendFriendRequest(String currentUser, Long targetUserId);

    Friendship acceptRequest( Long friendshipId);

    void declineRequest(Long friendshipId);

    Friendship blockUser( Long friendshipId);

    List<Friendship> getPendingReceivedRequests(String currentUser);

    List<Friendship> getPendingSentRequests(String currentUser);

    Friendship markAsChecked(Long friendshipId);

    List<User> getFriends(String currentUser);

    void removeFriend(Long friendshipId) ;
}
