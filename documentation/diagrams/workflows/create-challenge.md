## 1️⃣ Vue d’ensemble – Workflow utilisateur (plan de dev)

Un utilisateur authentifié peut :

1. **S’authentifier**

    * [OK]

2. **Créer un `ChallengeGroup`**

    * Saisir : le nom du groupe.
    * (Optionnel) Vérifier que l’utilisateur n’a pas déjà un challenge actif pour le mois en cours.
    * Générer / associer la **`ChallengePeriod` courante** (mois en cours).
    * Définir la **liste des `Task`** du groupe (`availableTasks`).
    * Définir la **liste des `Reward`** (`rewardPool`).
    * Configurer :

        * `rewardIsRandom`
        * `rewardIsRecurring`
    * Ajouter les **participants** (de son groupe de friendship y compris lui-même).

3. **Pendant le mois : jouer**

    * Consulter la page du jour (`ChallengeDay`).
    * Enregistrer des actions :

        * pour chaque tâche effectuée → créer une `TaskEntry`.
    * Les points s’accumulent dans `TaskEntry.points`.

4. **Fin de période (`ChallengePeriod`)**

    * Calculer les scores par joueur (somme des `TaskEntry.points` de la période).
    * Déterminer le **`winner`**.
    * Choisir la **`Reward`** pour la période :

        * soit via `rewardIsRandom`,
        * soit via `rewardIsRecurring`,
        * soit manuellement.
    * Mettre `rewardHonored` à `true` lorsque la récompense a été effectivement donnée.

**Phases de dev** :

1. Auth + boilerplate
2. Création de challenge
3. Enregistrement des tâches
4. Clôture / scoring / reward

---

## 2️⃣ Diagramme de séquence – Création d’un `ChallengeGroup`

Ici on montre :
**User authentifié → Front → Backend → Services → DB**

```mermaid
sequenceDiagram
    autonumber
    actor U as User (authentifié)
    participant UI as Frontend (SPA / UI)
    participant CGC as ChallengeGroupController
    participant CS as ChallengeService
    participant TS as TaskService
    participant RS as RewardService
    participant PS as PeriodService
    participant DB as Database

    Note over U,UI: L'utilisateur est déjà authentifié (JWT / session)

    U ->> UI: Ouvre la page "Créer un challenge"
    UI -->> U: Affiche le formulaire (nom, participants, tâches, récompenses, options...)

    U ->> UI: Remplit le formulaire et valide

    UI ->> CGC: POST /challenge-groups<br/>payload: name, tasks, rewards, participants,<br/>rewardIsRandom, rewardIsRecurring
    CGC ->> CS: createChallengeGroup(ownerId, dto)

    Note over CS: 1. Valider les données métier<br/>- name non vide<br/>- au moins 1 task<br/>- au moins 2 participants<br/>- cohérence des options de reward

    CS ->> PS: createCurrentPeriod()
    PS ->> PS: Calculer startDate / endDate<br/>du mois courant
    PS ->> DB: INSERT ChallengePeriod
    DB -->> PS: ChallengePeriod créée

    CS ->> DB: INSERT ChallengeGroup<br/>avec owner, name, flags reward
    DB -->> CS: ChallengeGroup créé (id)

    Note over CS,TS: 2. Créer / associer les Tasks

    CS ->> TS: createOrAttachTasks(challengeGroupId, dto.tasks)
    TS ->> DB: INSERT Task si nouvelle<br/>et associer au ChallengeGroup
    DB -->> TS: Tasks persistées
    TS -->> CS: 
%%    List&lt;Task&gt; availableTasks

    Note over CS,RS: 3. Créer / associer les Rewards

    CS ->> RS: createOrAttachRewards(challengeGroupId, dto.rewards)
    RS ->> DB: INSERT Reward si nouvelle<br/>et associer au ChallengeGroup
    DB -->> RS: Rewards persistées
    RS -->> CS: 
%%    List&lt;Reward&gt; rewardPool

    Note over CS,DB: 4. Associer les participants

    CS ->> DB: Associer les participants au ChallengeGroup<br/>(table de jointure ChallengeGroup_User)
    DB -->> CS: Participants associés

    Note over CS,PS: 5. Lier la période au groupe

    CS ->> DB: UPDATE ChallengePeriod<br/>SET group_id = challengeGroupId
    DB -->> CS: Période mise à jour

    CS -->> CGC: Retourne le ChallengeGroup complet<br/>(owner, participants, tasks, rewards, période)
    CGC -->> UI: 201 Created + données du challenge
    UI -->> U: Affiche la page du challenge créé
```

👉 Ce diagramme te donne **directement la liste des méthodes à créer** :

* `ChallengeService.createChallengeGroup(...)`
* `PeriodService.createCurrentPeriod()`
* `TaskService.createOrAttachTasks(...)`
* `RewardService.createOrAttachRewards(...)`
* plus les opérations `INSERT/UPDATE` associées côté repository / DAO.

---

## 3️⃣ Diagramme de séquence – Jouer un jour (enregistrer des tâches)

Workflow : *un user va sur la page du jour et enregistre ce qu’il a fait.*

```mermaid
sequenceDiagram
    autonumber
    actor U as User (joueur)
    participant UI as Frontend
    participant CDC as ChallengeDayController
    participant TES as TaskEntryService
    participant DS as DayService
    participant DB as Database

    U ->> UI: Ouvre la page "Challenge du jour"
    UI ->> CDC: GET /challenge/{groupId}/day?date=today
    CDC ->> DS: getOrCreateDay(groupId, date)
    DS ->> DB: SELECT ChallengeDay<br/>WHERE groupId = ? AND date = ?
    alt Day existe
        DB -->> DS: ChallengeDay trouvé
    else Day n'existe pas
        DS ->> DB: INSERT ChallengeDay<br/>lié à la ChallengePeriod courante
        DB -->> DS: ChallengeDay créé
    end
    DS -->> CDC: ChallengeDay (entries existantes, etc.)
    CDC -->> UI: Retourne le jour + entries

    U ->> UI: Sélectionne les tâches faites et valide
    UI ->> TES: POST /task-entries<br/>payload: { dayId, taskIds[], points (optionnel) }

    loop Pour chaque tâche sélectionnée
        TES ->> DB: INSERT TaskEntry<br/>(playerId, taskId, dayId, points)
        DB -->> TES: TaskEntry créée
    end

    TES -->> UI: Liste des TaskEntry créées
    UI -->> U: Affiche les points du jour mis à jour
```

**Règles métier impliquées ici :**

* Créer le `ChallengeDay` s’il n’existe pas encore.
* Vérifier que :

    * l’utilisateur est **participant du `ChallengeGroup`**,
    * la `Task` choisie fait bien partie de `ChallengeGroup.availableTasks`,
    * la date est bien dans la `ChallengePeriod` (on ne joue pas sur un mois déjà clôturé).

---

## 4️⃣ Diagramme de séquence – Clôture du mois (calcul du gagnant + reward)

La clôture peut être :

* soit déclenchée automatiquement (cron),
* soit lancée par le owner via un bouton "Clôturer la période".

```mermaid
sequenceDiagram
    autonumber
    actor U as Owner (optionnel)
    participant UI as Frontend
    participant CPC as ChallengePeriodController
    participant CPS as ChallengePeriodService
    participant TES as TaskEntryService
    participant RS as RewardService
    participant DB as Database

    U ->> UI: Clique sur "Clôturer la période"
    UI ->> CPC: POST /periods/{periodId}/close

    CPC ->> CPS: closePeriod(periodId)

    Note over CPS: 1. Récupérer tous les TaskEntry de la période

    CPS ->> TES: getEntriesForPeriod(periodId)
    TES ->> DB: SELECT TaskEntry JOIN ChallengeDay<br/>WHERE period_id = ?
    DB -->> TES: Liste&lt
%%    ; TaskEntry&gt
    TES -->> CPS: Entries de la période

    Note over CPS: 2. Calcul du score par joueur

    CPS ->> CPS: Agréger les points par playerId<br/>et déterminer le winner

    Note over CPS,RS: 3. Choix de la récompense

    CPS ->> DB: SELECT ChallengeGroup<br/>lié à la période
    DB -->> CPS: ChallengeGroup (flags + rewardPool)

    alt rewardIsRandom == true
        CPS ->> RS: pickRandomReward(rewardPool)
        RS ->> RS: Tirage aléatoire dans la liste
        RS -->> CPS: Reward choisie
    else rewardIsRecurring == true
        CPS ->> RS: getRecurringReward(rewardPool)
        RS -->> CPS: Reward (toujours la même)
    else
        Note over CPS: Reward choisie manuellement<br/>(ou via une autre logique)
    end

    Note over CPS,DB: 4. Mettre à jour la période

    CPS ->> DB: UPDATE ChallengePeriod<br/>SET winner_id = ?, reward_id = ?, rewardHonored = false
    DB -->> CPS: Période mise à jour

    CPS -->> CPC: Résumé (winner, reward, scores)
    CPC -->> UI: Retourne le résultat de la période
    UI -->> U: Affiche le gagnant + la récompense
```

Plus tard, quand la récompense est réellement donnée :

* tu auras un endpoint du style : `PATCH /periods/{id}/reward/honored`
* qui met `rewardHonored = true`.

---

## 5️⃣ Comment t’en servir concrètement pour le dev

### Étape 1 – Création de challenge

* [ ] Endpoint `POST /challenge-groups`
* [ ] `ChallengeService.createChallengeGroup(...)`
* [ ] `PeriodService.createCurrentPeriod(...)`
* [ ] `TaskService.createOrAttachTasks(...)`
* [ ] `RewardService.createOrAttachRewards(...)`
* [ ] Association des participants au `ChallengeGroup`

### Étape 2 – Gestion des jours & entrées

* [ ] Endpoint `GET /challenge/{id}/day?date=...`
* [ ] `DayService.getOrCreateDay(...)`
* [ ] Endpoint `POST /task-entries`
* [ ] `TaskEntryService.createEntries(...)`

### Étape 3 – Clôture de période

* [ ] Endpoint `POST /periods/{id}/close`
* [ ] `ChallengePeriodService.closePeriod(...)`
* [ ] `TaskEntryService.getEntriesForPeriod(...)`
* [ ] Calcul des scores + winner
* [ ] Choix de la reward (random / recurring / autre)
* [ ] Mise à jour de `winner`, `reward`, `rewardHonored = false`

