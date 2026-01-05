## Problème rencontré : initialisation H2 pour les tests + seed Admin en `@PostConstruct`

### Contexte

* En **prod**, l’application utilise une base **MySQL**.
* En **test**, on utilise une base **H2 in-memory** (profil `test`).
* On voulait :

    * initialiser la base de test H2 avec des données (rôles + users) via un script `data.sql`,
    * **et** garder la logique existante qui crée un admin en base via un `AdminGenerator` appelé en `@PostConstruct` dans la classe main.

Résultat : les tests plantaient ou avaient des comportements bizarres (doublons, erreurs SQL, etc.), et H2 ne se comportait pas comme attendu.

---

## 1. Problème côté H2 : scripts SQL et création du schéma

### Symptômes

* Spring Boot loggue :

```text
Error creating bean with name 'dataSourceScriptDatabaseInitializer'
No data scripts found at location 'classpath:data-mysql.sql'
```

puis, après renommage en `data.sql` :

```text
Failed to execute SQL script statement #1 of file [.../data.sql]
```

* Et côté H2 :

    * erreurs sur des tables non trouvées,
    * ou sur des contraintes.

### Cause

Par défaut :

1. Hibernate crée le schéma **en même temps** que le contexte démarre, selon `spring.jpa.hibernate.ddl-auto`.
2. Spring Boot joue les scripts `schema.sql` / `data.sql` à un moment précis du cycle de démarrage.

Si on ne configure rien, il peut arriver que :

* `data.sql` soit exécuté **avant** que Hibernate ait fini de créer les tables,
* ou que le nom du fichier (`data-mysql.sql`) ne soit simplement **pas pris en compte** par H2.

### Solution mise en place

Dans `application-test.properties` :

```properties
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL;NON_KEYWORDS=USER,ROLE
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.driver-class-name=org.h2.Driver

spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# IMPORTANT : pour que Hibernate crée d'abord le schéma, puis qu’on injecte les données
spring.jpa.defer-datasource-initialization=true

# Toujours exécuter data.sql si présent
spring.sql.init.mode=always
```

Et le script a été placé dans `src/main/resources` sous le nom **exact** : `data.sql`.

```sql
-- 1. Rôles
INSERT INTO role (id, name, created_at, updated_at)
VALUES
  (1, 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (2, 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 2. Utilisateurs
INSERT INTO users (id, username, password, enabled, email, created_at, updated_at)
VALUES
  (1, 'alice', 'password123', TRUE, 'alice@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (2, 'bob',   'password123', TRUE, 'bob@example.com',   CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (3, 'charlie','password123',FALSE,'charlie@example.com',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);

-- 3. Jointure user_role
INSERT INTO user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO user_role (user_id, role_id) VALUES (1, 2);
INSERT INTO user_role (user_id, role_id) VALUES (2, 2);
INSERT INTO user_role (user_id, role_id) VALUES (3, 2);
```

Dans les tests, on active bien le profil `test` :

```java
@SpringBootTest
@ActiveProfiles("test")
class FriendshipServiceImplTest {
    // ...
}
```

👉 Résultat : au démarrage des tests, Hibernate crée les tables dans H2, puis Spring exécute `data.sql`. On a une base de test propre et reproductible.

📚 Docs utiles :

* Initialisation des données : Spring Boot SQL init (`spring.sql.init.*`, `spring.jpa.defer-datasource-initialization`) ([Medium][1])
* Utilisation de H2 pour les tests ([Medium][1])

---

## 2. Problème avec `@PostConstruct` et le seed Admin

### Code existant

Dans la classe main :

```java
@SpringBootApplication
public class SlaveryHomeChallengeApiApplication {

    @Autowired
    private AdminGenerator adminGenerator;

    public static void main(String[] args) {
        SpringApplication.run(SlaveryHomeChallengeApiApplication.class, args);
    }

    // @PostConstruct
    // void init() {
    //     adminGenerator.seedRolesAndAdmin();
    // }
}
```

`AdminGenerator.seedRolesAndAdmin()` fait en gros :

* création des rôles (`ADMIN`, `USER`),
* création d’un utilisateur **admin**,
* associations rôles ↔ admin.

### Symptômes en test

* En prod, ça va : on veut un admin seedé.
* En test :

    * **conflit** possible avec les données injectées via `data.sql` (doublons sur `username` / `email` / contraintes),
    * tests qui dépendent malgré eux de la logique de seed de prod,
    * difficulté à maîtriser exactement l’état de la base.

En gros : le code de prod (seed admin) s’exécute **aussi en test**, alors qu’en test on veut une base **maîtrisée** par `data.sql`.

---

## 3. Solution : isoler le seed admin avec les profils Spring

On a laissé tomber `@PostConstruct` dans la classe main, et on a déplacé le seed dans un composant conditionné par un **profil**.

### Étape 1 – Créer une config de seed avec @Profile

```java
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import jakarta.annotation.PostConstruct;

@Configuration
@Profile("!test") // actif partout SAUF quand le profil 'test' est actif
public class AdminSeedConfig {

    private final AdminGenerator adminGenerator;

    public AdminSeedConfig(AdminGenerator adminGenerator) {
        this.adminGenerator = adminGenerator;
    }

    @PostConstruct
    public void initAdmin() {
        adminGenerator.seedRolesAndAdmin();
    }
}
```

* `@Configuration` : classe de config Spring.
* `@Profile("!test")` : ce bean **n’est pas chargé** quand on lance l’app avec le profil `test`.
  Il est actif pour `dev`, `prod`, etc.
* `@PostConstruct` : appelé **une seule fois** après l’initialisation du bean → idéal pour un seed simple.

📚 Doc profils Spring : ([YouTube][2])

### Étape 2 – Tests : profil `test`

Dans les tests :

```java
@SpringBootTest
@ActiveProfiles("test")
class FriendshipServiceImplTest {
    // ...
}
```

Avec `@ActiveProfiles("test")` :

* `AdminSeedConfig` (profil `!test`) **n’est pas chargé**,
* donc `seedRolesAndAdmin()` **ne s’exécute pas**,
* on n’a que ce que `data.sql` a mis en base : **parfait pour écrire des tests prévisibles**.

---

## 4. Pourquoi cette approche fonctionne bien

1. **Séparation nette prod / test**

    * Prod / dev : seed admin via `AdminSeedConfig` + base MySQL.
    * Test : pas de seed admin, juste `data.sql` sur H2.

2. **Base de test déterministe**

    * Tu sais exactement quels users et rôles existent au début des tests (ceux de `data.sql`),
    * Pas d’effet de bord caché venant d’un `@PostConstruct` orienté prod.

3. **Profils Spring = configuration propre**

    * Le même code applicatif,
    * Mais des comportements adaptés selon l’environnement (`application.properties`, `application-test.properties`, `@Profile`, `@ActiveProfiles`).

4. **Facile à maintenir**

    * Si tu changes la façon de seed l’admin en prod, tu touches `AdminSeedConfig` et éventuellement `AdminGenerator`, **mais pas les tests**.
    * Si tu veux enrichir les jeux de données de test, tu modifies `data.sql` sans impacter la prod.

---


[1]: https://medium.com/%40AlexanderObregon/using-spring-boot-with-h2-for-lightweight-testing-f8121b725ebc?utm_source=chatgpt.com "Using Spring Boot with H2 for Lightweight Testing"
[2]: https://www.youtube.com/watch?v=Y26ZZApHMX4&utm_source=chatgpt.com "Mastering Spring Profiles: Annotations and Practical ..."
