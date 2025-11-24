# 🤝 Fonctionnalité Friendship (Relations d’amitié entre utilisateurs)

## 📘 Introduction

La fonctionnalité **Friendship** permet de gérer les relations d’amitié entre utilisateurs au sein de l’application.
Une relation d’amitié correspond à une entrée dans l’entité `Friendship`, reliant deux utilisateurs via un statut (PENDING, ACCEPTED, DECLINED ou BLOCKED).

Cette fonctionnalité constitue une base indispensable pour permettre aux utilisateurs de se connecter entre eux, d’interagir dans l’interface, et plus tard de participer ensemble à des `ChallengeGroup`.

---

## 🧱 Entité Friendship

Une relation d’amitié entre deux utilisateurs est représentée par **une seule ligne** dans la table `friendship`.

```java
enum FriendshipStatus {
    PENDING,
    ACCEPTED,
    DECLINED,
    BLOCKED
}
```

Champs principaux :

* `User requester` → utilisateur qui envoie la demande
* `User receiver` → utilisateur qui reçoit la demande
* `FriendshipStatus status`
* `boolean isChecked` → notification vue par l’envoyeur
* `createdAt` / `updatedAt` (timestamps auto via `@CreationTimestamp` et `@UpdateTimestamp`)

---

# 🔍 Fonctionnalités utilisateur

### 1. Recherche d’utilisateur

* Un utilisateur peut rechercher d’autres utilisateurs via une recherche `contains` sur le `username`.
* L’utilisateur courant ne peut **pas** apparaître dans les résultats (prévention d’auto-demande).

---

### 2. Envoi d’une demande d’ami

* L’utilisateur peut envoyer une demande d’amitié à un autre utilisateur.
* Création d’une entrée `Friendship` :

    * `status = PENDING`
    * `isChecked = false`
* Un e-mail est envoyé au destinataire.
* Une notification apparaît dans l’interface du destinataire.

Règles métier :

* Impossible d’envoyer une demande vers soi-même.
* Impossible d’avoir plusieurs demandes PENDING entre les mêmes utilisateurs.
* Une seule relation Friendship par paire d’utilisateurs.

---

### 3. Gestion d’une demande reçue

Le destinataire peut :

* **Accepter** la demande → `status = ACCEPTED`
* **Refuser** la demande → `status = DECLINED`
* **Bloquer** l’utilisateur → `status = BLOCKED`

---

### 4. Notification pour l’envoyeur (si ACCEPTED)

* Si la demande est acceptée :

    * `isChecked = false` déclenche une notification côté envoyeur.
    * Tant que `isChecked == false`, la notification reste visible.
    * Lorsqu’elle est consultée → `isChecked = true`.

Les notifications vues ne disparaissent pas :
elles restent accessibles dans un **historique simple** basé sur `Friendship`.

---

### 5. Gestion post-acceptation

Une fois l’amitié établie :

* Les deux utilisateurs apparaissent dans la **liste d’amis** l’un de l’autre.
* L’un ou l’autre peut :

    * **Rompre l’amitié**
    * **Bloquer** l’autre utilisateur

> 🔐 Le modèle garantit qu’une seule entrée `Friendship` représente la relation.

---

# 🧩 Endpoints prévus (backend)

👉 À rédiger en détail lors de l’implémentation, mais voici les grandes lignes :

* `GET /users/search?query=xxx`
* `POST /friendships` (envoyer une demande)
* `GET /friendships/pending` (demandes reçues en attente)
* `PATCH /friendships/{id}/accept`
* `PATCH /friendships/{id}/decline`
* `PATCH /friendships/{id}/block`
* `PATCH /friendships/{id}/check` (l’envoyeur a vu la notification)

---

# 📋 TODO – Dépendances non implémentées / à faire plus tard

Ces points nécessitent que d’autres fonctionnalités soient mises en place avant d’être développés :

### ❌ Dépendants d’autres modules (ChallengeGroup, Notifications, Historique…)

* [ ] **Blocage d’un utilisateur** : définir l’impact exact sur `ChallengeGroup`.
  Exemple : que faire si deux utilisateurs bloqués sont dans le même challenge ?
* [ ] **Refactor notifications** : centraliser les notifications (mail + UI) dans un module dédié.
* [ ] **Historique des événements** : créer une entité ou un module dédié pour stocker toutes les notifications passées (pas seulement Friendship).
* [ ] **Limiter les actions selon les relations** : ex. un utilisateur bloqué ne doit plus pouvoir rejoindre un challenge d’un autre.
* [ ] **Restrictions dans ChallengeGroup** :

    * Obliger que tous les participants d’un challenge soient amis. (à la creation du groupe, tous les utilisateur deviennent automatiquement amis)
    * Empêcher d’ajouter quelqu’un avec une relation bloquée ou déclinée.
* [ ] **Suppression d’amitié** : définir précisément si on supprime la ligne ou si on passe à un statut `REMOVED` (à discuter selon le métier).

