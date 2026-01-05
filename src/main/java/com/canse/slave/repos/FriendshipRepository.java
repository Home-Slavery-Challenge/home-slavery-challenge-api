package com.canse.slave.repos;

import com.canse.slave.entities.Friendship;
import com.canse.slave.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    @Query("SELECT f FROM Friendship f WHERE  f.status = 'PENDING' AND f.receiver.username = :currentUser")
    List<Friendship> getPendingsReceivedRequestsByUser(@Param("currentUser") String currentUser);

    @Query("""
            SELECT f FROM Friendship f
            WHERE f.status = 'PENDING'
              AND f.requester.username = :currentUser
            """)
    List<Friendship> getPendingsSentRequests(@Param("currentUser") String currentUser);

    @Query("""
            SELECT f FROM Friendship f
            WHERE f.requester.username = :currentUser
            """)
    List<Friendship> getAllFriendshipByRequester(@Param("currentUser") String currentUser);

    @Query("""
            SELECT f FROM Friendship f
            WHERE f.requester.id = :userId
            """)
    List<Friendship> getAllFriendshipByReceiver(@Param("userId") Long userId);


    @Query("""
            SELECT f.receiver
            FROM Friendship f
            WHERE f.status = :status
              AND f.requester.username = :currentUser
            """)
    List<Users> findFriendsOfUser(@Param("currentUser") String currentUser, @Param("status") String status);

    @Query("""
            SELECT f FROM Friendship f
            WHERE f.requester.id = :requesterId
              AND f.receiver.id = :receiverId
            """)
    Friendship getAlreadyExistsFriendship(@Param("requesterId") Long requesterId, @Param("receiverId") Long receiverId);

    @Query("""
                SELECT f FROM Friendship f
                WHERE (f.requester.id = :userId1 AND f.receiver.id = :userId2)
                   OR (f.requester.id = :userId2 AND f.receiver.id = :userId1)
                ORDER BY f.createdAt DESC
            """)
    List<Friendship> findAllFriendshipsBetween(@Param("userId1") Long userId1, @Param("userId2") Long userId2);


    Friendship findByReceiverId(Long userId);

    @Query("""
            SELECT f FROM Friendship f
            WHERE f.status = 'ACCEPTED'
              AND (f.requester.username = :currentUser OR f.receiver.username = :currentUser)
            """)
    List<Friendship> findAcceptedFriendshipsOfUser(@Param("currentUser") String currentUser);


    @Query("""
            SELECT DISTINCT f.receiver
            FROM Friendship f
            WHERE f.status = 'BLOCKED'
              AND f.requester.username = :currentUser
            """)
    List<Users> findBlockedUsersOf(@Param("currentUser") String currentUser);


    @Modifying
    @Transactional
    @Query("""
        DELETE FROM Friendship f
        WHERE f.receiver.id = :receiverId
          AND f.requester.username = :currentUser
        """)
    void deleteFriendshipByCurrenttargetRequester(
            @Param("currentUser") String currentUser,
            @Param("receiverId") Long receiverId
    );

}
