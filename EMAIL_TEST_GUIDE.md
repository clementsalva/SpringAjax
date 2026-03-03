# Test d'envoi d'emails vers Gmail

Ce guide explique comment tester l'envoi d'emails vers l'adresse `lamouscouli.labostrofa@gmail.com`.

## Prérequis

Pour envoyer un email via Gmail avec Spring Mail, tu dois:

1. **Créer un mot de passe d'application Gmail**
   - Aller sur: https://myaccount.google.com/apppasswords
   - Sélectionner "Mail" et "Windows" 
   - Google générera un mot de passe de 16 caractères
   - Copier ce mot de passe (c'est celui qu'on utilisera, pas le mot de passe Gmail principal)

2. **Configurer les variables d'environnement**

   Méthode 1: Via PowerShell
   ```powershell
   $env:GMAIL_USER="votre.email@gmail.com"
   $env:GMAIL_PASSWORD="votre-mot-de-passe-app"
   ```

   Méthode 2: Via des paramètres Maven
   ```bash
   mvn test -Dtest=SendEmailGmailTest -DskipTests=false -DGMAIL_USER="votre.email@gmail.com" -DGMAIL_PASSWORD="votre-mot-de-passe-app"
   ```

## Exécuter le test

### Option 1: Utiliser le script batch (Windows)
```batch
set GMAIL_USER=votre.email@gmail.com
set GMAIL_PASSWORD=votre-mot-de-passe-app
test-email.bat
```

### Option 2: Exécuter directement avec Maven
```batch
mvn test -Dtest=SendEmailGmailTest -DskipTests=false -DGMAIL_USER="votre.email@gmail.com" -DGMAIL_PASSWORD="votre-mot-de-passe-app"
```

### Option 3: Utiliser le profil Spring Boot
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=test -Dspring-boot.run.arguments="--GMAIL_USER=votre.email@gmail.com --GMAIL_PASSWORD=votre-mot-de-passe-app"
```

## Que se passera-t-il

1. Le test se lancera avec la classe `SendEmailGmailTest`
2. Un email sera envoyé à `lamouscouli.labostrofa@gmail.com` 
3. Tu verras dans la console:
   - "✓ Email envoyé avec succès!" si ça marche
   - Ou un message d'erreur si ça ne marche pas

4. Tu pourras vérifier dans ta boîte Gmail que l'email a été reçu

## Configuration du fichier

Pour envoyer depuis une adresse Gmail différente, modifie le fichier:
`src/test/resources/application-test.properties`

Exemple pour envoyer depuis `noreply@tonentreprise.com`:
```properties
mail.from.email=noreply@tonentreprise.com
```

## Dépannage

### Erreur "535 5.7.8 Username and password not accepted"
- Vérifie que tu utilises un **mot de passe d'application** et pas ton mot de passe Google principal
- Va sur https://myaccount.google.com/apppasswords pour en générer un nouveau

### Erreur "Connection refused"
- Vérifie ta connexion Internet
- Le port 587 doit être accessible (peut être bloqué par un pare-feu)

### Pas de réception d'email
- Vérifie dans les spams/indésirables
- Le serveur Gmail peut les filtrer comme spam au départ

## Code source

Les fichiers créés pour ce test sont:

1. **`src/test/java/pharmacie/service/SendEmailGmailTest.java`** - Test unitaire
2. **`src/test/resources/application-test.properties`** - Configuration Spring Mail
3. **`test-email.bat`** - Script batch pour faciliter le lancement

Pour activer le test, modifie la classe `SendEmailGmailTest` et enlève l'annotation `@Disabled`.
