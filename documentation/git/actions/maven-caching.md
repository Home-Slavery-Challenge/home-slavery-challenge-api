
```yml
  - name: Cache Maven dependencies
    uses: actions/cache@v4
    with:
      path: |
        ~/.m2/repository
      key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}
      restore-keys: |
        ${{ runner.os }}-maven-
```

---

# 💡 À quoi sert `Cache Maven dependencies` ?

Lors d’un `mvn install` ou `mvn test`, Maven télécharge **toutes les dépendances** du projet dans :

```
~/.m2/repository
```

Cela peut peser plusieurs centaines de Mo et prendre **10 à 40 secondes** par run GitHub Actions.

👉 **Le but de cette étape est donc d’éviter que Maven retélécharge tout à chaque workflow.**
GitHub Actions va stocker ces dépendances dans un **cache**, et les restaurer au prochain run.

---

# 🔍 Décomposition ligne par ligne

### 🔹 1. Nom du step

```yaml
- name: Cache Maven dependencies
```

Juste un nom lisible dans l’interface GitHub Actions.

---

### 🔹 2. Utilisation de l’action officielle cache

```yaml
uses: actions/cache@v4
```

On utilise le système de cache de GitHub nativement.

---

### 🔹 3. Dossier à mettre en cache

```yaml
path: |
  ~/.m2/repository
```

→ C’est **le dossier local Maven contenant toutes les dépendances** téléchargées.

---

### 🔹 4. Génération de la clé du cache

```yaml
key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}
```

Cette clé détermine **si le cache doit être utilisé** ou **si on doit en créer un nouveau**.

Décomposition :

* `${{ runner.os }}` → Linux, Windows ou Mac (évite les conflits)
* `maven-` → préfixe
* `${{ hashFiles('**/pom.xml') }}` → hash **du fichier pom.xml**

➡️ Ça veut dire :
**si le `pom.xml` change, le cache doit être régénéré**, car tes dépendances ont peut-être changé.

---

### 🔹 5. restore-keys (fallback)

```yaml
restore-keys: |
  ${{ runner.os }}-maven-
```

Si la clé exacte n’existe pas, GitHub va essayer de trouver un cache partiel compatible, par exemple :

* `linux-maven-123456…` (clé exacte)
* ou si introuvable → `linux-maven-` (tous les caches Maven pour Linux)

➡️ Ainsi, même si `pom.xml` change, GitHub va réutiliser un cache proche au lieu de repartir de zéro.

---

# 🧠 En résumé (simple)

| Élément        | Rôle                                                                      |
| -------------- | ------------------------------------------------------------------------- |
| `path`         | Le dossier où Maven stocke les libs                                       |
| `key`          | Empêche le cache d'être réutilisé si `pom.xml` a changé                   |
| `restore-keys` | Permet de réutiliser un cache “proche” si la clé exacte n’est pas trouvée |

---

# 📈 Avantage

Sans cache :
⏳ Maven télécharge tout → 20–40 secondes

Avec cache :
⚡ exécution immédiate → 1–2 secondes

➡️ **Ton workflow devient beaucoup plus rapide** et plus économe
