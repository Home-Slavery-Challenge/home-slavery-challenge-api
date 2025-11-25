package com.canse.slave.repos;

import com.canse.slave.entities.Friendship;
import com.canse.slave.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    @Query("SELECT f FROM Friendship f WHERE  f.status = 'PENDING' AND f.receiver.username = :currentUser")
    List<Friendship> getPendingReceivedRequestsByUser(@Param("currentUser") String currentUser);

    @Query("SELECT f FROM Friendship f WHERE f.isChecked = FALSE AND f.requester.username = :currentUser")
    List<Friendship> getPendingSentRequests(@Param("currentUser") String currentUser);

    @Query("""
                SELECT f.receiver
                FROM Friendship f
                WHERE f.status = 'ACCEPTED'
                  AND f.requester.username = :currentUser
            """)
    List<User> findFriendsOfUser(@Param("currentUser") String currentUser);

}
