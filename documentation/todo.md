
# ✅ **TODO – Slavery Home Challenge API**


# 🔥 **1. Sécurité serveur & infrastructure (Priorité haute)**

### 🔐 SSH & accès système

* [ ] Passer le serveur **en authentification par clé SSH uniquement**
* [ ] Désactiver totalement la connexion root (`PermitRootLogin no`)
* [ ] Créer un **utilisateur non-root** dédié au déploiement
* [ ] Configurer le firewall server (`ufw allow 22, 80, 443`)
* [ ] Configurer le firewall provider (`ufw allow 22, 80, 443`)
* [ ] Installer **Fail2Ban** pour bloquer les IP suspectes
* [ ] Configurer automatic security updates (Ubuntu unattended-upgrades)

### 🛡️ HTTPS & Reverse Proxy

* [ ] Générer un certificat HTTPS automatique avec **Caddy** (recommandé)
  ou
* [ ] Installer **Nginx reverse proxy + Certbot**
* [ ] Redirection HTTPS → HTTP, gestion du firewall
* [ ] Configurer le proxy pour forward vers Docker (`app:8080`)

---

# 📦 **2. Docker & Livraison continue (Priorité haute)**

### 🐳 Architecture Docker

* [ ] Créer un **réseau Docker dédié** : backend / db isolés du reste
* [ ] Modifier docker-compose pour utiliser ce réseau privé
* [ ] Empêcher MySQL d’exposer le port 3306 en public
* [ ] Préparer docker-compose.prod.yml pour multi-environnements

### 🔄 Fiabilité & résilience

* [ ] Ajouter stratégie de **rollback automatique** :

    * Si le conteneur crash → revenir à l'image `previous`
    * Automatiser via `docker buildx imagetools` + script fallback

---

# 🧪 **3. Tests & Qualité logicielle**

### Unitaires + intégration

* [ ] Ajouter **Testcontainers** pour faire tourner MySQL dans un conteneur pendant les tests
* [ ] Ajouter tests d’intégration API (ex : User creation / login / seed)
* [ ] Ajouter tests du mailer (mock SMTP)

### Qualité & outils

* [ ] Ajouter **Checkstyle** ou **Spotless** pour le lint Java
* [ ] Ajouter analyse statique SonarQube (optionnel)

---

# 📊 **4. Monitoring & observabilité**

### 🧩 Solution complète

* [ ] Déployer **Prometheus** (metrics)
* [ ] Déployer **Grafana** (visualisation)
* [ ] Installer **cAdvisor** pour monitorer les containers
* [ ] Ajouter un dashboard : CPU / RAM / DB / Réponses API

(Option : installation dans un autre docker-compose dédié au monitoring)

---

# ☸️ **5. Évolution : Kubernetes (optionnel mais pro)**

* [ ] Migrer docker-compose vers des manifests Kubernetes :

    * Deployment API
    * StatefulSet MySQL
    * Service LoadBalancer
* [ ] Ajouter Ingress + Cert-Manager
* [ ] Ajouter autoscaling (HPA)

---

# 🗄️ **6. Base de données : Durabilité & sauvegarde**

### 🚑 Backups de la DB

* [ ] Script automatisé de sauvegarde MySQL chaque nuit à minuit
* [ ] Rotation → garder 7 ou 30 jours
* [ ] Envoi des backups :

    * soit vers **un autre serveur**,
    * soit vers un bucket S3
* [ ] Test de restauration (obligatoire)

---

# 🌟 **7. Améliorations projet (à planifier ensuite)**

* [ ] Mettre en place un système de logs centralisés (Loki ou ELK)
* [ ] Ajouter un système d'alertes (Discord / Slack / Email)
* [ ] Setup d’une vraie Release Strategy (tags, changelogs, versions semver)
* [ ] Ajouter un Makefile pour simplifier les commandes (dev / prod / test)

---

