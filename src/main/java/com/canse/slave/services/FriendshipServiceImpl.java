package com.canse.slave.services;

import com.canse.slave.entities.Friendship;
import com.canse.slave.entities.User;
import com.canse.slave.enums.FriendshipStatus;
import com.canse.slave.repos.FriendshipRepository;
import com.canse.slave.repos.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// TODO : Créer des DTO pour exposer les données au front

@Transactional
@Service
public class FriendshipServiceImpl implements FriendshipService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendshipRepository friendshipRepository;

    /**
     * Récupère une relation d'amitié via son identifiant ou lève une exception
     * si la relation n'existe pas.
     */
    private Friendship getFriendshipOrThrow(Long friendshipId) {
        return friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new IllegalArgumentException("Friendship not found: " + friendshipId));
    }

    /**
     * Recherche des utilisateurs par nom, en excluant l'utilisateur courant des résultats.
     *
     * @param query       fragment de nom / username à rechercher
     * @param currentUser username de l'utilisateur courant
     * @return liste des utilisateurs correspondant à la recherche, sans inclure currentUser
     */
    @Override
    public List<User> searchUsersByName(String query, String currentUser) {
        return userRepository.findByUsernameContainsIgnoreCase(query).stream()
                .filter(u -> !u.getUsername().equals(currentUser))
                .toList();
    }

    /**
     * Envoie une demande d'amitié de l'utilisateur courant vers l'utilisateur cible.
     * <p>
     * Si une relation existe déjà dans ce sens (currentUser → targetUser),
     * la relation existante est retournée sans en créer une nouvelle.
     *
     * @param currentUser  username de l'utilisateur courant (demandeur)
     * @param targetUserId identifiant de l'utilisateur cible
     * @return la relation d'amitié existante ou nouvellement créée
     */
    @Override
    public Friendship sendFriendRequest(String currentUser, Long targetUserId) {

        User userRequester = userRepository.findByUsername(currentUser);
        if (userRequester == null) {
            throw new IllegalArgumentException("User not found: " + currentUser);
        }

        User userReceiver = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("Target user not found: " + targetUserId));

        Friendship friendshipAlreadyExist =
                friendshipRepository.getAlreadyExistsFriendship(userRequester.getId(), userReceiver.getId());
        if (friendshipAlreadyExist != null) {
            return friendshipAlreadyExist;
        }

        Friendship friendship = new Friendship();
        friendship.setRequester(userRequester);
        friendship.setReceiver(userReceiver);
        friendship.setChecked(false);
        friendship.setStatus(FriendshipStatus.PENDING);
        return friendshipRepository.save(friendship);
    }

    /**
     * Accepte une demande d'amitié et normalise les doublons éventuels.
     * <p>
     * À partir de l'identifiant d'une relation :
     * - récupère toutes les relations entre les deux utilisateurs (A→B et B→A),
     * - conserve la plus récente,
     * - supprime les autres,
     * - met la relation conservée au statut ACCEPTED et checked = true.
     *
     * @param currentUser  username de l'utilisateur courant (utilisé pour le contexte / sécurité éventuelle)
     * @param friendshipId identifiant de la relation utilisée pour valider l'amitié
     * @return la relation d'amitié acceptée
     */
    @Override
    public Friendship acceptAndNormalizeFriendship(String currentUser, Long friendshipId) {

        Friendship friendship = getFriendshipOrThrow(friendshipId);

        User userA = friendship.getRequester();
        User userB = friendship.getReceiver();

        List<Friendship> friendships =
                friendshipRepository.findAllFriendshipsBetween(userA.getId(), userB.getId());

        if (friendships.isEmpty()) {
            throw new RuntimeException("No friendship request found between these users");
        }

        Friendship toKeep = friendships.get(0);

        if (friendships.size() > 1) {
            for (int i = 1; i < friendships.size(); i++) {
                friendshipRepository.delete(friendships.get(i));
            }
        }

        toKeep.setStatus(FriendshipStatus.ACCEPTED);
        toKeep.setChecked(true);

        return friendshipRepository.save(toKeep);
    }

    /**
     * Décline une demande d'amitié et supprime toutes les relations
     * entre les deux utilisateurs (A→B et B→A) encore en base.
     *
     * @param friendshipId identifiant de la relation à refuser
     */
    @Override
    public void declineRequest(Long friendshipId) {
        Friendship friendship = getFriendshipOrThrow(friendshipId);

        Long userA = friendship.getRequester().getId();
        Long userB = friendship.getReceiver().getId();

        // Récupère A→B et B→A
        List<Friendship> allRelations =
                friendshipRepository.findAllFriendshipsBetween(userA, userB);

        // Supprime toutes les relations entre ces deux utilisateurs
        friendshipRepository.deleteAll(allRelations);
    }

    /**
     * Bloque n'importe quel utilisateur à partir de son identifiant,
     * par exemple depuis une recherche de contacts.
     * <p>
     * Règle métier pour BLOCKED :
     * - requester = utilisateur bloqué
     * - receiver  = utilisateur qui bloque
     * <p>
     * Aucun lien d'amitié préalable n'est nécessaire.
     */
    @Override
    public Friendship blockUser(Long userIdToBlock, String currentUser) {

        User blocker = userRepository.findByUsername(currentUser);
        if (blocker == null) {
            throw new IllegalArgumentException("Current user not found: " + currentUser);
        }

        User blocked = userRepository.findById(userIdToBlock)
                .orElseThrow(() -> new IllegalArgumentException("User to block not found: " + userIdToBlock));

        Friendship friendship = new Friendship();
        friendship.setRequester(blocked);
        friendship.setReceiver(blocker);
        friendship.setChecked(true);
        friendship.setStatus(FriendshipStatus.BLOCKED);

        return friendshipRepository.save(friendship);
    }

    /**
     * Méthode historique utilisée pour gérer un blocage à partir d'une relation.
     * <p>
     * Actuellement, cette méthode supprime uniquement les relations entre deux utilisateurs
     * (doublon PENDING A→B / B→A ou relation unique), sans créer d'entrée BLOCKED.
     * La logique métier de blocage est désormais gérée par {@link #blockUser(Long, String)}.
     *
     * @param friendshipId identifiant de la relation
     * @param currentUser  username de l'utilisateur courant
     */
    @Override
    public void blockFriendship(Long friendshipId, String currentUser) {

        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new RuntimeException("Friendship not found"));

        User a = friendship.getRequester();
        User b = friendship.getReceiver();

        List<Friendship> friendshipList =
                friendshipRepository.findAllFriendshipsBetween(a.getId(), b.getId());

        if (friendshipList.size() > 1) {
            Friendship friendshipA = friendshipRepository.findByReceiverId(a.getId());
            Friendship friendshipB = friendshipRepository.findByReceiverId(b.getId());

            if (friendshipA != null) {
                friendshipRepository.delete(friendshipA);
            }
            if (friendshipB != null) {
                friendshipRepository.delete(friendshipB);
            }
        } else {
            friendshipRepository.deleteById(friendshipId);
        }
    }

    /**
     * Débloque un utilisateur en supprimant l'entrée représentant le blocage.
     * <p>
     * Seule la relation de blocage (BLOCKED) est supprimée. Il sera nécessaire
     * de recréer une nouvelle relation d'amitié si besoin par la suite.
     *
     * @param friendshipId identifiant de la relation de blocage à supprimer
     */
    @Override
    public void unblockUser(Long friendshipId) {
        Friendship friendship = getFriendshipOrThrow(friendshipId);
        friendshipRepository.delete(friendship);
    }

    /**
     * Récupère la liste des demandes d'amitié reçues et encore en attente (PENDING).
     *
     * @param currentUser username de l'utilisateur courant
     * @return liste des demandes reçues en attente
     */
    @Override
    public List<Friendship> getPendingReceivedRequests(String currentUser) {
        return friendshipRepository.getPendingsReceivedRequestsByUser(currentUser);
    }

    /**
     * Récupère la liste des demandes d'amitié envoyées par l'utilisateur
     * et encore en attente (PENDING).
     *
     * @param currentUser username de l'utilisateur courant
     * @return liste des demandes envoyées en attente
     */
    @Override
    public List<Friendship> getPendingSentRequests(String currentUser) {
        return friendshipRepository.getPendingsSentRequests(currentUser);
    }

    /**
     * Marque une demande d'amitié comme "vue" (checked = true),
     * par exemple pour faire disparaître un badge de notification dans l'interface.
     *
     * @param friendshipId identifiant de la relation à marquer comme vue
     * @return la relation mise à jour
     */
    @Override
    public Friendship markAsChecked(Long friendshipId) {
        Friendship friendship = getFriendshipOrThrow(friendshipId);
        friendship.setChecked(true);
        return friendshipRepository.save(friendship);
    }

    /**
     * Récupère la liste des amis de l'utilisateur courant, c'est-à-dire
     * toutes les relations ACCEPTED où il est soit requester, soit receiver.
     *
     * @param currentUser username de l'utilisateur courant
     * @return liste des utilisateurs amis
     */
    @Override
    public List<User> getFriends(String currentUser) {
        return friendshipRepository.findAcceptedFriendsOfUser(currentUser);
    }

    /**
     * Récupère la liste des utilisateurs bloqués par l'utilisateur courant.
     * <p>
     * Rappel de la convention :
     * - requester = utilisateur bloqué
     * - receiver  = utilisateur qui bloque
     *
     * @param currentUser username de l'utilisateur courant
     * @return liste des utilisateurs bloqués
     */
    @Override
    public List<User> getBlocked(String currentUser) {
        return friendshipRepository.findBlockedUsersOf(currentUser);
    }

    /**
     * Supprime définitivement une relation d'amitié, quelle que soit son statut.
     *
     * @param friendshipId identifiant de la relation à supprimer
     */
    @Override
    public void removeFriend(Long friendshipId) {
        friendshipRepository.deleteById(friendshipId);
    }
}
