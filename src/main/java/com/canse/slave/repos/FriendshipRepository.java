package com.canse.slave.repos;

import com.canse.slave.dto.UserRefDto;
import com.canse.slave.entities.Friendship;
import com.canse.slave.entities.Users;
import com.canse.slave.projections.FriendshipLiteProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    @Query("""
              select f
              from Friendship f
              where f.status = com.canse.slave.enums.FriendshipStatus.PENDING
                and f.receiver.username = :currentUser
            """)
    List<FriendshipLiteProjection> getPendingsReceivedRequestsByUser(@Param("currentUser") String currentUser);

    @Query("""
            SELECT f FROM Friendship f
            WHERE f.status = 'PENDING'
              AND f.requester.username = :currentUser
            """)
    List<FriendshipLiteProjection> getPendingsSentRequests(@Param("currentUser") String currentUser);

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
                select distinct new com.canse.slave.dto.UserRefDto(u.id, u.username)
                from Friendship f
                join f.receiver u
                where f.status = com.canse.slave.enums.FriendshipStatus.BLOCKED
                  and f.requester.username = :currentUser
            """)
    List<UserRefDto> findBlockedUsersOf(@Param("currentUser") String currentUser);


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

    @Query("""
                select f from Friendship f
                where (f.requester.id = :currentId and f.receiver.id = :targetId)
                   or (f.requester.id = :targetId and f.receiver.id = :currentId)
            """)
    List<Friendship> findAllBetweenUsers(@Param("currentId") Long currentId,
                                         @Param("targetId") Long targetId);

    @Modifying
    @Transactional
    @Query("""
              update Friendship f
              set f.isChecked = true
              where f.status = com.canse.slave.enums.FriendshipStatus.PENDING
                and f.receiver.username = :currentUser
            """)
    void markPendingReceivedAsChecked(@Param("currentUser") String currentUser);
}
