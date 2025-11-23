# 📘 **Slavery Home Challenge – Documentation Fonctionnelle & Technique**

Le **Slavery Home Challenge** est une application ludique permettant à plusieurs joueurs de s’affronter dans un challenge mensuel basé sur l’accomplissement de tâches quotidiennes (ménage, entretien, etc.).
Chaque action réalisée rapporte des points, et à la fin du mois un gagnant est désigné et reçoit une récompense.

Cette documentation présente :

* le fonctionnement du jeu,
* les règles principales,
* la structure de données,
* les entités et leurs relations.

---

# 🎮 1. Fonctionnement Général du Jeu

## 📅 **Un challenge mensuel**

* Le jeu se déroule **par mois**.
* Un joueur (appelé *owner*) crée un **ChallengeGroup**.
* Ce groupe peut contenir **deux joueurs ou plus**.
* Le challenge commence au **début du mois en cours**, même s’il est créé en milieu de mois.
* À la fin du mois, la période se termine automatiquement.

## 👤 **Owner et participants**

* Le **owner** crée le groupe et configure :

    * les participants,
    * la liste des tâches disponibles (`Task`),
    * la liste des récompenses (`Reward`),
    * le mode de sélection de la récompense :

        * aléatoire (`rewardIsRandom = true`)
        * ou récurrente (`rewardIsRecurring = true`)
* Les autres joueurs rejoignent en tant que **participants**.

---

# 🧽 2. Mécanique de Jeu Quotidienne

Chaque jour du mois est représenté par un **ChallengeDay**.

### 🔹 Dans une journée :

* Un ou plusieurs joueurs peuvent effectuer une ou plusieurs **tâches**.
* Chaque tâche accomplie donne lieu à une **TaskEntry**.
* Une `TaskEntry` inclut :

    * la tâche effectuée,
    * le joueur,
    * le jour,
    * le nombre de points obtenus.

### 🔹 Exemple :

* Joueur A fait :

    * Aspirateur (1 point)
    * Serpillière (1 point)
* Joueur B fait :

    * Poussière (1 point)

Ce jour-là :

* Joueur A : 2 points
* Joueur B : 1 point

---

# 🏆 3. Fin de mois : gagnant et récompense

À la fin d’une période (`ChallengePeriod`) :

* les points accumulés sont totalisés,
* un **gagnant** est désigné,
* une **récompense** est attribuée.

### 🎁 Gestion des récompenses

La récompense d’un mois est :

* choisie dans le `rewardPool`,
* soit de façon **aléatoire** (si `rewardIsRandom = true`),
* soit **répétée automatiquement** chaque mois (si `rewardIsRecurring = true`),
* soit laissée au choix manuel du owner.

Chaque récompense mensuelle est ensuite liée à une **période** via `ChallengePeriod.reward`.

Enfin, un booléen `rewardHonored` indique si la récompense a effectivement été donnée au gagnant.

---

# 🏗 4. Structure technique – Entités et Relations

Consulter le repertoire documentation/diagrams/entities

---

# 🧩 5. Description des Entités (détaillée)

## 👤 **User**

Représente un joueur de l'application.
Contient l’authentification, les rôles, et les jetons de vérification.

## 🏠 **ChallengeGroup**

Le groupe de joueurs participant ensemble au challenge mensuel.

Contient :

* owner
* participants
* les tâches disponibles
* les récompenses disponibles
* les périodes (les mois de jeu)
* les paramètres de récompense

## 📅 **ChallengePeriod**

Représente **un mois de jeu**.

Contient :

* le mois (startDate, endDate)
* les jours
* le gagnant
* la récompense du mois
* le statut de la récompense (`rewardHonored`)

## 🗓 **ChallengeDay**

Un jour dans un mois du challenge.

## 🏷 **Task**

Une tâche que les joueurs peuvent effectuer (aspirateur, serpillière, etc.).

## 📌 **TaskEntry**

L’action d’un joueur qui effectue une tâche un jour donné.

## 🎁 **Reward**

Une récompense pouvant être attribuée à un gagnant.
