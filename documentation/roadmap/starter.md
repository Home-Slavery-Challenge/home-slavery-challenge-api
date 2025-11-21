# 🧭 Roadmap complète (organisée)

---

# **Phase 0 — Foundations / Mise en place du projet **

1. **Initialiser l’application**

    * Créer le projet Spring Boot
    * Structurer les packages
    * Mettre en place les dépendances (Spring Web, Spring Data, Security, Mail, Validation…)

2. **Mettre en place le système de login / register**

    * Endpoints register + login
    * Validation des inputs
    * Gestion des erreurs

3. **Configurer la sécurité de base**

    * Spring Security (JWT ou Session)
    * PasswordEncoder (BCrypt)
    * Protection des routes
    * Auth / Roles

4. **Mettre en place la configuration `.env`**

    * Variables DB
    * Variables mail
    * Variables JWT
    * Ajouter `.env` au `.gitignore`

5. **Créer le système de seed initial**

    * Seed des rôles (`ADMIN`, `USER`)
    * Seed d’un admin principal (depuis `.env`)
    * Mise en place d’un `AdminGenerator` ou `CommandLineRunner`

6. **Mettre en place le service mail**

    * SMTP configuré via `.env`
    * Envoyer un email de validation de compte
    * Templates mail basiques
    * Endpoint activation du compte

7. **Créer le repo GitHub**

    * Initial commit
    * Push du projet avec `.gitignore` et `.env.example`

8. **Créer la base du README**

    * Description du projet
    * Installation
    * `.env`
    * Lancement
    * Petites sections (Tech stack, Roadmap début)

---


## 🧩 Phase 1 – Modélisation & conception

1. **Créer les schémas UML des bases de données**

    * Clarifier les entités, relations, clés, contraintes.

---

## 🌿 Phase 2 – Organisation du code & du repo

2. **Mettre en place un branching model**

    * Par ex. : `main` / `dev` / features (`feature/xyz`) / `hotfix/...`.
    * Décider : qui merge où, et à quel moment.

3. **Créer des règles de sécurité sur le repo (branch protection)**

    * Interdiction de push direct sur `main` / `prod`.
    * Obligation de passer par PR.
    * Option : review obligatoire, tests verts, etc.

---

## 🐳 Phase 3 – Dockerisation

4. **Créer un Dockerfile pour l’environnement local et tester en local**

    * Objectif : `docker build` + `docker run` → l’app tourne correctement.
    * Valider : connexion BDD, variables d’env, ports, logs…

5. **Adapter / créer un Dockerfile pour la prod (ou un Dockerfile unique avec args/profiles)**

    * Gérer : profil `prod`, variables d’env sécurisées, config BDD de prod, etc.
    * Tester : image compatible avec l’environnement cible (serveur, orchestration éventuelle).

---

## ⚙️ Phase 4 – CI/CD (GitHub Actions)

> Ici, on part du principe que Docker est déjà OK en local.

6. **Configurer GitHub Actions pour lancer les tests sur la branche `dev`**

    * Workflow déclenché sur `push` / `pull_request` vers `dev`.
    * Étapes : checkout → build → tests (backend, frontend si besoin).

7. **Configurer GitHub Actions pour build & déployer sur le serveur quand on push sur la branche `prod` (ou `main`)**

    * Étapes typiques :

        * Build de l’image Docker
        * Push sur registry (Docker Hub / GHCR)
        * Déploiement sur le serveur (ssh, docker compose, etc.)

8. **Configurer les filtres pour ignorer certains fichiers dans les workflows GitHub Actions**

    * Exemple : ne pas relancer build/deploy si seuls des fichiers de doc / README / `.md` changent.
    * Utiliser `paths` / `paths-ignore` dans les workflows.

---

## 👨🏻‍🎓 Phase 5 – Update de la doc

9. **Mettre à jour le readme**

10. **Mettre en place un sommaire pour la documentation**

