# Spring Boot Application - Gestion Pharmaceutique

## Description

Ce projet est une application **Spring Boot** destinée à une pharmacie centrale permettant à des dispensaires de commander des médicaments.

L’API fournie comprend :
- La gestion des médicaments
- La gestion des dispensaires
- La gestion des commandes
- La gestion des catégories

Elle utilise :
- Des entités JPA pour interagir avec la base de données
- Des services REST exposés via des contrôleurs
- Des dépôts Spring Data JPA pour l’accès aux données

---

# Fonctionnalités

- Gestion des médicaments, dispensaires, commandes et catégories
- Suivi des statistiques de commandes par catégorie
- Exposition des données via des services REST (JSON et XML)
- API de téléchargement d’images pour les médicaments
- Support CORS activé pour toutes les origines

---

# Modèles de données

## Modèle conceptuel de données

![Modèle conceptuel de données](images/modele_conceptuel.png)

## Modèle logique de données

![Modèle logique de données](images/modele_logique.png)

Spring Data JPA génère automatiquement le modèle relationnel conforme aux annotations des entités.

---

# Structure du code

## Couche Accès aux données

### Entités

- **Medicament**
- **Dispensaire**
- **Commande**
- **Ligne**
- **Categorie**

### Repositories

- MedicamentRepository
- DispensaireRepository
- CommandeRepository
- LigneRepository

---

## Couche Services métier

- **CommandeService**
  - Vérification des stocks
  - Calcul des totaux
  - Gestion des dispensaires

---

## Couche Web

### Contrôleurs REST

- CommandeController
- StatisticsRestController
- SimpleRestController

### Contrôleurs MVC

- StatsMVCController (Thymeleaf)

---

# Documentation API

Swagger / OpenAPI :

http://localhost:8080/swagger-ui.html

---

# Technologies utilisées

- Java 21 LTS
- Spring Boot 3.5.3
- Spring Data JPA (Hibernate 6.6.18)
- Jakarta Persistence API 3.1
- Lombok 1.18.42
- SpringDoc OpenAPI 2.7.0
- H2 (développement)
- PostgreSQL (production)

---

# Démarrage

## Prérequis

- Java 21
- Maven 3.6+

## Lancer l'application

mvn clean spring-boot:run

---

# Extension du projet : Gestion des approvisionnements

## 1. Amélioration du modèle de données

### Fournisseurs

Ajouter une entité **Supplier** :

- id
- nom
- email

Relation many-to-many :

- Un fournisseur peut fournir plusieurs catégories
- Une catégorie peut avoir plusieurs fournisseurs

Chaque catégorie doit être associée à au moins deux fournisseurs à l’initialisation.

---

## 2. Service métier d’approvisionnement

### Détection des médicaments à réapprovisionner

Condition :

unitesEnStock < niveauDeReappro

### Envoi d’emails via SendGrid

Contraintes :

- Un fournisseur reçoit un seul email
- Email récapitulatif par catégorie
- Clé API via variable d’environnement :
  SENDGRID_API_KEY
- Ne jamais hardcoder la clé

---

## 3. REST Controller

Exposer un endpoint permettant de déclencher le service d’approvisionnement.

---

## 4. Déploiement

- Déploiement sur Render
- Base PostgreSQL
- Variables d’environnement pour :
  - DATABASE_URL
  - DB_USERNAME
  - DB_PASSWORD
  - SENDGRID_API_KEY
  - PORT