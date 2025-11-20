# 📘 Mini-guide Spring : `@Component`, `@Service`, `@Configuration`, `@Bean`

> **Objectif** : comprendre exactement *quand* utiliser quelle annotation et pourquoi.

---

# 1. `@Component` — Le bean Spring de base

### ✔️ Ce que c’est

Une classe gérée automatiquement par Spring.
Elle est instanciée **une seule fois** (singleton par défaut) et injectable via `@Autowired` ou constructeur.

### ✔️ Quand l’utiliser

Quand tu veux créer **un service / utilitaire métier** dans ton application.

### ✔️ Exemple

```java
@Component
public class AdminGenerator {
    public void seed() { ... }
}
```

### 🔗 Docs

[https://docs.spring.io/spring-framework/reference/core/beans/classpath-scanning.html](https://docs.spring.io/spring-framework/reference/core/beans/classpath-scanning.html)

---

# 2. `@Service` — Variante de `@Component`

### ✔️ Ce que c’est

Identique à `@Component`, mais sémantiquement pour les **services métier**.

### ✔️ Quand l’utiliser

Pour une classe qui représente une **logique métiers** (UserService, EmailService…).

### ✔️ Exemple

```java
@Service
public class UserService { ... }
```

### 🔗 Docs

[https://docs.spring.io/spring-framework/reference/core/beans/classpath-scanning.html#beans-stereotype-annotations](https://docs.spring.io/spring-framework/reference/core/beans/classpath-scanning.html#beans-stereotype-annotations)

---

# 3. `@Repository` — Variante pour la persistance

### ✔️ Ce que c’est

Aussi un `@Component`, mais Spring ajoute des comportements liés aux exceptions.

### ✔️ Quand l’utiliser

Pour tout ce qui interagit avec la BDD (JPA, JDBC…).

### ✔️ Exemple

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {}
```

---

# 4. `@Configuration` — Classe de configuration Spring

### ✔️ Ce que c’est

Une classe dont les méthodes `@Bean` **créent et retournent des objets gérés par Spring**.

### ✔️ Quand l’utiliser

Quand tu veux enregistrer **manuellement** un bean que Spring ne peut pas créer tout seul.

Exemples typiques :

* `PasswordEncoder`
* `ObjectMapper`
* `CorsConfigurationSource`

### ✔️ Exemple

```java
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### 🔗 Docs

[https://docs.spring.io/spring-framework/reference/core/beans/java/registering-bean-definitions.html](https://docs.spring.io/spring-framework/reference/core/beans/java/registering-bean-definitions.html)

---

# 5. `@Bean` — Crée un bean manuellement

### ✔️ Ce que c’est

Une méthode dans une classe `@Configuration` qui **retourne un objet** que Spring va gérer.

### ✔️ Quand l’utiliser

Quand :

* tu veux configurer un objet toi-même,
* ou que Spring ne peut pas l’instancier seul.

### ✔️ Exemple

```java
@Bean
public ModelMapper modelMapper() {
    return new ModelMapper();
}
```

⚠️ **Une méthode `@Bean` doit retourner quelque chose.**
Ce n'est pas fait pour exécuter du code, mais pour créer un **bean**.

---

# 6. `@PostConstruct` — Code exécuté après le démarrage du contexte

### ✔️ Ce que c’est

Une méthode exécutée **juste après que Spring ait construit le bean et injecté ses dépendances**.

### ✔️ Quand l’utiliser

Pour un petit initialiseur simple (ex : seed, log…)

### ✔️ Exemple

```java
@PostConstruct
void init() {
    adminGenerator.seed();
}
```

### 🔗 Docs

[https://docs.oracle.com/javaee/7/api/javax/annotation/PostConstruct.html](https://docs.oracle.com/javaee/7/api/javax/annotation/PostConstruct.html)
*(Même annotation en Jakarta EE dans Spring 6+)*

---

# 7. `CommandLineRunner` / `ApplicationRunner`

### ✔️ Ce que c’est

Interfaces appelées **après le démarrage complet de Spring Boot**.

### ✔️ Quand l’utiliser

Pour exécuter du code "au lancement" *une fois que tout est prêt*.

### ✔️ Exemple

```java
@Component
public class StartupRunner implements CommandLineRunner {

    public void run(String... args) {
        System.out.println("App démarrée !");
    }
}
```

### 🔗 Docs

[https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.command-line-runner](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.command-line-runner)

---

# 8. Résumé : Quand utiliser quoi ?

| Besoin                                               | Annotation                 |
| ---------------------------------------------------- | -------------------------- |
| Classe métier simple                                 | `@Component`               |
| Service métier                                       | `@Service`                 |
| Accès base de données                                | `@Repository`              |
| Créer un bean manuellement                           | `@Configuration` + `@Bean` |
| Besoin d'exécuter du code APRES injection            | `@PostConstruct`           |
| Besoin d'exécuter du code APRÈS le démarrage complet | `CommandLineRunner`        |

---

# 9. Exemple concret, simple et complet

### 1️⃣ Un bean défini manuellement :

```java
@Configuration
public class BeanConfig {

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### 2️⃣ Un composant métier :

```java
@Component
public class AdminGenerator {
    private final UserService userService;
    private final PasswordEncoder encoder;

    public AdminGenerator(UserService userService, PasswordEncoder encoder) {
        this.userService = userService;
        this.encoder = encoder;
    }

    public void seed() {
        // seed logic
    }
}
```

### 3️⃣ Initialisation au lancement :

```java
@Component
public class Startup implements CommandLineRunner {
    private final AdminGenerator generator;

    public Startup(AdminGenerator generator) {
        this.generator = generator;
    }

    @Override
    public void run(String... args) {
        generator.seed();
    }
}
```

---

# 10. Règles d’or 🌟

* **Jamais de logique métier dans une méthode `@Bean`**.
* **Évite `@Autowired` sur les champs** → préfère l’injection par **constructeur**.
* `@Component` = universel
* `@Service` = composant métier
* `@Configuration` = configuration technique
* `@PostConstruct` = petit init
* `CommandLineRunner` = init plus propre au démarrage
* `@Bean` = création manuelle d’un objet Spring
* **Ne mélange pas logique métier et configuration**.

