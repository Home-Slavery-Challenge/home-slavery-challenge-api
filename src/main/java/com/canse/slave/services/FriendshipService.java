package com.canse.slave.services;

import com.canse.slave.dto.UserRefDto;
import com.canse.slave.entities.Friendship;
import com.canse.slave.entities.Users;
import com.canse.slave.projections.FriendshipLiteProjection;

import java.util.List;

public interface FriendshipService {


    List<Friendship> getAllFriendshipByRequester(String currentUser);

    List<UserRefDto> searchUsersByName(String query, String currentUser);

    void sendFriendRequest(String currentUser, Long targetUserId);

    Friendship acceptAndNormalizeFriendship(String currentUser, Long otherUserId);

    void declinePendingRequest(Long friendshipId);

    void declineFriendship(Long userIdTarget, String currentUser);

    void blockUser(Long friendshipId, String currentUser);

    void blockFriendship(Long friendshipId, String currentUser);

    void unblockUser(Long friendshipId, String currentUser);

    List<FriendshipLiteProjection> getPendingReceivedRequests(String currentUser);

    List<FriendshipLiteProjection> getPendingSentRequests(String currentUser);

    void markAsChecked(String currentUsername);

    List<Users> getFriends(String currentUser);

    List<UserRefDto> getSummaryFriends(String currentUser);

    List<UserRefDto> getBlocked(String currentUser);

    void removeFriend(Long friendshipId);
}
