# 🎯 Gestion des URLs de base de données entre local, Docker et production

## 📌 Problématique

Spring Boot doit utiliser une URL différente selon l’environnement :

* **Local hors Docker** → la base tourne sur la machine → `localhost`
* **Docker (local + prod)** → la base tourne dans un conteneur → `db`
* **Production (avec ou sans Docker)** → l’hôte peut être différent → `db`, un domaine, ou une IP

Difficulté initiale :
Un seul `.env` était utilisé partout, ce qui obligeait à **modifier manuellement** la variable `DATASOURCE_URL` ou à **commenter / décommenter** des lignes selon le mode d’exécution.

---

# ✅ Solution mise en place

### ➤ **1. Utiliser une variable générique : `DB_HOST`**

Dans `application.properties` :

```properties
spring.datasource.url=jdbc:mysql://${DB_HOST:localhost}:3306/slavery-home-challenge-api?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=${DATASOURCE_USERNAME}
spring.datasource.password=${DATASOURCE_PASSWORD}
```

---

### ➤ **2. `.env` local (sans Docker)**

Ce fichier sert pour l’exécution locale classique (via IntelliJ, Maven…).

```env
DB_HOST=localhost

DATASOURCE_USERNAME=slaveapp
DATASOURCE_PASSWORD=slavepass
MYSQL_DATABASE=slavery-home-challenge-api

# + autres variables (Admin, mail…)
```

---

### ➤ **3. Docker Compose surcharge uniquement `DB_HOST`**

Dans `docker-compose.yml` :

```yaml
services:
  app:
    env_file:
      - .env
    environment:
      DB_HOST: db  # 👈 
      DATASOURCE_USERNAME: ${MYSQL_USER} # 👀🔎
      DATASOURCE_PASSWORD: ${MYSQL_PASSWORD} # 👀🔎
```

---

### ➤ **4. Production**

Sur le serveur, on place un `.env` dédié contenant :

```env
DB_HOST=db        # ou une URL réelle si la DB est externe
DATASOURCE_USERNAME=prod_user
DATASOURCE_PASSWORD=prod_pass
```

En prod :

* même `docker-compose.yml`
* seul le `.env` change

---

# 🧠 Résumé visuel

| Environnement       | Valeur utilisée                      | Source                |
| ------------------- | ------------------------------------ | --------------------- |
| Local (sans Docker) | `DB_HOST=localhost`                  | `.env`                |
| Local Docker        | `DB_HOST=db`                         | override dans compose |
| Production          | adapté au setup (`db`, IP, hostname) | `.env` prod           |



---

### 👀🔎 Note — Pourquoi Spring ne se connectait pas à MySQL en Docker ?

Le problème ne venait **pas** d'une interdiction d'utiliser `root`.
Le problème venait simplement du fait que :

* **MySQL Docker** utilisait
  `MYSQL_USER=slaveapp` / `MYSQL_PASSWORD=slavepass`
* **Spring Boot**, lui, utilisait
  `DATASOURCE_USERNAME=root` / `DATASOURCE_PASSWORD=Rootoorn`

➡ Résultat : Spring tentait de se connecter avec **de mauvais identifiants**, d’où l’erreur *"Access denied / Communications link failure"*.

**Solution :**
Dans `docker-compose.yml`, on mappe les identifiants MySQL vers ceux de Spring :

```yaml
environment:
  DATASOURCE_USERNAME: ${MYSQL_USER}
  DATASOURCE_PASSWORD: ${MYSQL_PASSWORD}
```

Ainsi :

* Spring utilise toujours le bon utilisateur dans Docker
* Le mode local reste indépendant
* Plus aucun conflit entre `.env` et MySQL du container

