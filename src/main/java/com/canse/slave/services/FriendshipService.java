package com.canse.slave.services;

import com.canse.slave.entities.Friendship;
import com.canse.slave.entities.Users;

import java.util.List;

public interface FriendshipService {


    List<Friendship> getAllFriendshipByRequester(String currentUser );
    List<Users> searchUsersByName(String query, String currentUser);

    Friendship sendFriendRequest(String currentUser, Long targetUserId);

    Friendship acceptAndNormalizeFriendship(String currentUser, Long otherUserId);

    void declinePendingRequest(Long friendshipId);
    void declineFriendship(Long userIdTarget,String currentUser);

    Friendship blockUser(Long friendshipId, String currentUser);

    void blockFriendship(Long friendshipId, String currentUser);

    void unblockUser(Long friendshipId,String currentUser);

    List<Friendship> getPendingReceivedRequests(String currentUser);

    List<Friendship> getPendingSentRequests(String currentUser);

    void markAsChecked(String currentUsername);

    List<Users> getFriends(String currentUser);

    List<Users> getBlocked(String currentUser);

    void removeFriend(Long friendshipId);
}
