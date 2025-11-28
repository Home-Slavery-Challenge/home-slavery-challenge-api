# ✅ Annotations pour `createdAt` et `updatedAt`

### 👉 Avec Hibernate (le plus simple et le plus utilisé dans Spring Boot)

```java
@CreationTimestamp
private LocalDateTime createdAt;

@UpdateTimestamp
private LocalDateTime updatedAt;
```

### 🔧 Exemple complet dans une entité

```java
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ... autres champs (requester, receiver, status...)

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
```

---

# 🟢 Ce que ça fait automatiquement

## ✔ `@CreationTimestamp`

➡ Remplit le champ **uniquement lors de l’INSERT**.
Aucune mise à jour lors des UPDATE.

## ✔ `@UpdateTimestamp`

➡ Met automatiquement à jour la valeur **à chaque UPDATE** dans la DB.

---

# ⚠️ Notes importantes

1. Ces annotations sont fournies par **Hibernate**, pas par JPA pur.
2. Elles fonctionnent dès que tu utilises Spring Boot + Hibernate (ce qui est ton cas).
3. Tu peux utiliser `LocalDateTime`, `Instant` ou `Date`.

---


### 🔧 Utiliser Spring Security pour récupérer l’utilisateur courant

Quand tu utilises Spring Security avec un JWT, l’utilisateur authentifié est généralement chargé dans le contexte de sécurité. Tu peux ensuite y accéder très facilement dans tes contrôleurs.

### ✨ Exemple simple avec `@AuthenticationPrincipal`

Tu peux annoter un paramètre de méthode de ton contrôleur avec `@AuthenticationPrincipal`. Par exemple :

```java
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

@RestController
@RequestMapping("/api")
public class MyController {

    @GetMapping("/whoami")
    public String whoAmI(@AuthenticationPrincipal UserDetails userDetails) {
        return "You are: " + userDetails.getUsername();
    }
}
```

### 🛠️ Ce qui se passe en coulisses

* Spring va automatiquement injecter l’utilisateur courant (dérivé du JWT) dans `userDetails`.
* Tu n’as pas besoin de créer une annotation personnalisée : c’est une pratique standard et bien documentée.

### 📦 Dépendances nécessaires

* Tu as juste besoin de Spring Security configuré pour utiliser les JWT.
* Les classes comme `UserDetails` ou `User` (si tu as une implémentation custom) sont déjà prévues pour ça.

### 🚀 En résumé

Utilise simplement `@AuthenticationPrincipal` dans tes endpoints, et tu pourras identifier facilement qui effectue la requête grâce au JWT. Pas besoin de réinventer la roue !
