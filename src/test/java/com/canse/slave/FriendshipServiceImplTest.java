package com.canse.slave;

import com.canse.slave.entities.Friendship;
import com.canse.slave.entities.Users;
import com.canse.slave.enums.FriendshipStatus;
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

    // =========================================================
    // searchUsersByName
    // =========================================================
    @Test
    void searchUsersByName_shouldReturnMatchingUsers_excludingCurrentUser() {
        List<Users> result = friendshipService.searchUsersByName("a", "alice");

        assertFalse(result.isEmpty());
        assertEquals(result.size(), 1);
        assertTrue(result.stream().anyMatch(u -> u.getUsername().equals("charlie")));
        assertTrue(result.stream().noneMatch(u -> u.getUsername().equals("alice")));
    }

    @Test
    void searchUsersByName_shouldReturnEmptyList_whenNoMatch() {
        List<Users> result = friendshipService.searchUsersByName("zzzzz", "alice");
        assertTrue(result.isEmpty());
    }

    // =========================================================
    // sendFriendRequest
    // =========================================================
    @Test
    void sendFriendRequest_shouldCreateNewFriendship_whenNoExistingRelation() {
        Users alice = userRepository.findByUsername("alice");
        Users charlie = userRepository.findByUsername("charlie");
        long countBefore = friendshipRepository.count();

        Friendship friendship = friendshipService.sendFriendRequest("alice", charlie.getId());

        assertNotNull(friendship.getId());
        assertEquals(alice.getId(), friendship.getRequester().getId());
        assertEquals(charlie.getId(), friendship.getReceiver().getId());
        assertEquals(FriendshipStatus.PENDING, friendship.getStatus());
        assertFalse(friendship.isChecked());
        assertEquals(countBefore + 1, friendshipRepository.count());
    }

    @Test
    void sendFriendRequest_shouldReturnExistingFriendship_whenRelationAlreadyExists() {
        Users bob = userRepository.findByUsername("bob");
        Users alice = userRepository.findByUsername("alice");

        Friendship existing = friendshipRepository
                .findAllFriendshipsBetween(bob.getId(), alice.getId())
                .get(0);

        long countBefore = friendshipRepository.count();

        Friendship result = friendshipService.sendFriendRequest("bob", alice.getId());

        assertEquals(countBefore, friendshipRepository.count());
        assertEquals(existing.getId(), result.getId());
    }

    // =========================================================
    // acceptAndNormalizeFriendship
    // =========================================================
    @Test
    void acceptAndNormalizeFriendship_shouldKeepSingleAcceptedRelationBetweenUsers() {
        Users alice = userRepository.findByUsername("alice");
        Users bob = userRepository.findByUsername("bob");

        Friendship f1 = new Friendship();
        f1.setRequester(alice);
        f1.setReceiver(bob);
        f1.setStatus(FriendshipStatus.PENDING);
        f1.setChecked(false);
        f1 = friendshipRepository.save(f1);

        Friendship f2 = new Friendship();
        f2.setRequester(bob);
        f2.setReceiver(alice);
        f2.setStatus(FriendshipStatus.PENDING);
        f2.setChecked(false);
        friendshipRepository.save(f2);

        Friendship accepted = friendshipService.acceptAndNormalizeFriendship("alice", f1.getId());

        List<Friendship> allBetween =
                friendshipRepository.findAllFriendshipsBetween(alice.getId(), bob.getId());

        assertEquals(1, allBetween.size());
        Friendship remaining = allBetween.get(0);
        assertEquals(FriendshipStatus.ACCEPTED, remaining.getStatus());
        assertTrue(remaining.isChecked());
        assertEquals(accepted.getId(), remaining.getId());
    }

    // =========================================================
    // declineRequest
    // =========================================================
    @Test
    void declineRequest_shouldDeleteAllFriendshipsBetweenUsers() {
        Users alice = userRepository.findByUsername("alice");
        Users bob = userRepository.findByUsername("bob");

        Friendship seeded =
                friendshipRepository.findAllFriendshipsBetween(alice.getId(), bob.getId()).get(0);

        friendshipService.declinePendingRequest(seeded.getId());

        List<Friendship> remaining =
                friendshipRepository.findAllFriendshipsBetween(alice.getId(), bob.getId());

        assertTrue(remaining.isEmpty());
    }

    // =========================================================
    // blockUser
    // =========================================================
//    @Test
//    void blockUser_shouldCreateBlockedFriendship() {
//        Users alice = userRepository.findByUsername("alice");
//        Users bob = userRepository.findByUsername("bob");
//        long countBefore = friendshipRepository.count();
//
//        Friendship blocked = friendshipService.blockUser(bob.getId(), "alice");
//
//        assertNotNull(blocked.getId());
//        assertEquals(alice.getId(), blocked.getRequester().getId());
//        assertEquals(bob.getId(), blocked.getReceiver().getId());
//        assertEquals(FriendshipStatus.BLOCKED, blocked.getStatus());
//        assertTrue(blocked.isChecked());
//        assertEquals(countBefore + 1, friendshipRepository.count());
//    }

    // =========================================================
    // blockFriendship
    // =========================================================
    @Test
    void blockFriendship_shouldDeleteExistingRelationsAndCreateSingleBlocked() {
        Users alice = userRepository.findByUsername("alice");
        Users bob = userRepository.findByUsername("bob");

        Friendship f1 = new Friendship();
        f1.setRequester(alice);
        f1.setReceiver(bob);
        f1.setStatus(FriendshipStatus.PENDING);
        f1.setChecked(false);
        f1 = friendshipRepository.save(f1);

        Friendship f2 = new Friendship();
        f2.setRequester(bob);
        f2.setReceiver(alice);
        f2.setStatus(FriendshipStatus.PENDING);
        f2.setChecked(false);
        friendshipRepository.save(f2);

        friendshipService.blockFriendship(f1.getId(), "alice");

        List<Friendship> allBetween =
                friendshipRepository.findAllFriendshipsBetween(alice.getId(), bob.getId());

        assertEquals(1, allBetween.size());
        Friendship blocked = allBetween.get(0);
        assertEquals(FriendshipStatus.BLOCKED, blocked.getStatus());
        assertTrue(blocked.isChecked());
        assertEquals(alice.getId(), blocked.getRequester().getId());
        assertEquals(bob.getId(), blocked.getReceiver().getId());
    }

    // =========================================================
    // unblockUser
    // =========================================================
//    @Test
//    void unblockUser_shouldDeleteBlockedFriendship() {
//        Users alice = userRepository.findByUsername("alice");
//        Users bob = userRepository.findByUsername("bob");
//
//        Friendship blocked = friendshipService.blockUser(bob.getId(), "alice");
//        Long id = blocked.getId();
//
//        friendshipService.unblockUser(id);
//
//        assertTrue(friendshipRepository.findById(id).isEmpty());
//    }

    // =========================================================
    // getPendingReceivedRequests / getPendingSentRequests
    // =========================================================
    @Test
    void getPendingReceivedRequests_shouldReturnRequestsForCurrentUser() {
        List<Friendship> received = friendshipService.getPendingReceivedRequests("alice");

        assertFalse(received.isEmpty());
        assertTrue(received.stream().allMatch(f -> f.getStatus() == FriendshipStatus.PENDING));
        assertTrue(received.stream().anyMatch(f ->
                f.getReceiver().getUsername().equals("alice") &&
                        f.getRequester().getUsername().equals("bob")
        ));
    }

    @Test
    void getPendingSentRequests_shouldReturnRequestsOfCurrentUser() {
        List<Friendship> sent = friendshipService.getPendingSentRequests("bob");

        assertFalse(sent.isEmpty());
        assertTrue(sent.stream().allMatch(f -> f.getStatus() == FriendshipStatus.PENDING));
        assertTrue(sent.stream().anyMatch(f ->
                f.getRequester().getUsername().equals("bob")
        ));
    }

    // =========================================================
    // markAsChecked
    // =========================================================
//    @Test
//    void markAsChecked_shouldSetCheckedToTrue() {
//        Users alice = userRepository.findByUsername("alice");
//        Users bob = userRepository.findByUsername("bob");
//
//        Friendship seeded =
//                friendshipRepository.findAllFriendshipsBetween(alice.getId(), bob.getId()).get(0);
//
//        assertFalse(seeded.isChecked());
//
//        Friendship updated = friendshipService.markAsChecked(seeded.getId());
//
//        assertTrue(updated.isChecked());
//        assertTrue(friendshipRepository.findById(seeded.getId()).get().isChecked());
//    }

    // =========================================================
    // getFriends
    // =========================================================
    @Test
    void getFriends_shouldReturnAcceptedFriendsForUser() {
        Users alice = userRepository.findByUsername("alice");
        Users bob = userRepository.findByUsername("bob");

        Friendship f = new Friendship();
        f.setRequester(alice);
        f.setReceiver(bob);
        f.setStatus(FriendshipStatus.PENDING);
        f.setChecked(false);
        f = friendshipRepository.save(f);

        friendshipService.acceptAndNormalizeFriendship("alice", f.getId());

        List<Users> friendsOfAlice = friendshipService.getFriends("alice");
        List<Users> friendsOfBob = friendshipService.getFriends("bob");

        assertTrue(friendsOfAlice.stream().anyMatch(u -> u.getUsername().equals("bob")));
        assertTrue(friendsOfBob.stream().anyMatch(u -> u.getUsername().equals("alice")));
    }

    // =========================================================
    // getBlocked
    // =========================================================
    @Test
    void getBlocked_shouldReturnUsersBlockedByCurrentUser() {
        Users alice = userRepository.findByUsername("alice");
        Users bob = userRepository.findByUsername("bob");
        assertNotNull(alice);
        assertNotNull(bob);

        friendshipService.blockUser(bob.getId(), "alice");

        List<Users> blockedByAlice = friendshipService.getBlocked("alice");

        assertFalse(blockedByAlice.isEmpty());
        assertTrue(blockedByAlice.stream().anyMatch(u -> u.getUsername().equals("bob")));
        assertTrue(blockedByAlice.stream().noneMatch(u -> u.getUsername().equals("alice")));
    }

    // =========================================================
    // removeFriend
    // =========================================================
    @Test
    void removeFriend_shouldDeleteFriendshipById() {
        Users alice = userRepository.findByUsername("alice");
        Users bob = userRepository.findByUsername("bob");

        Friendship f = new Friendship();
        f.setRequester(alice);
        f.setReceiver(bob);
        f.setStatus(FriendshipStatus.ACCEPTED);
        f.setChecked(true);
        f = friendshipRepository.save(f);

        Long id = f.getId();
        friendshipService.removeFriend(id);

        assertTrue(friendshipRepository.findById(id).isEmpty());
    }
}
