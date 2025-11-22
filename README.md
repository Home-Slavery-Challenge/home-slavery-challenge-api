# 🧹 Home Slavery Competition

Application Spring Boot permettant de gérer et noter les tâches ménagères hebdomadaires (et d’autres fonctionnalités à venir).

Ce projet a pour objectif de simplifier la gestion du foyer en suivant les tâches, leurs fréquences, les responsables et un scoring hebdomadaire.

## 🚀 Technologies

TODO
---

# 📦 Installation & Lancement

## 1. Cloner le projet

```sh
git clone https://github.com/ton-repo.git
cd household-tasks-tracker
```

Voici la **section mise à jour** de ton README, propre, claire et structurée :

---

## 2. Configurer le fichier `.env`

Créer un fichier `.env` à la racine du projet.
Ce fichier contient **toutes les variables nécessaires**, autant pour l’exécution **locale** que pour **Docker**.

```env
# ----------------------------
# MySQL (Docker)
# ----------------------------
MYSQL_ROOT_PASSWORD=RootStrongPassword
MYSQL_USER=slaveapp
MYSQL_PASSWORD=slavepass
MYSQL_DATABASE=slavery-home-challenge-api

# ----------------------------
# Spring datasource
# ----------------------------
DB_HOST=localhost
DATASOURCE_USERNAME=root
DATASOURCE_PASSWORD=root

# ----------------------------
# Admin seed user
# ----------------------------
ADMIN_USERNAME=admin_username
ADMIN_PASSWORD=admin_password
ADMIN_EMAIL=admin@email.domain

# ----------------------------
# Mail service
# ----------------------------
MAIL_HOST=server.name.domain
MAIL_PORT=SMTP_PORT
MAIL_USERNAME=admin@email.domain
MAIL_PASSWORD=password_app
```
---


TODO 