@echo off
REM Script pour tester l'envoi d'email via Gmail
REM Usage: test-email.bat
REM Les variables d'environnement GMAIL_USER et GMAIL_PASSWORD doivent être définies

echo Envoi d'un email de test via Spring Mail vers Gmail...
echo.

if "%GMAIL_USER%"=="" (
    echo ERREUR: La variable GMAIL_USER n'est pas définie
    echo.
    echo Instructions:
    echo 1. Aller sur https://myaccount.google.com/apppasswords
    echo 2. Sélectionner "Mail" et "Windows"
    echo 3. Générer un mot de passe d'application
    echo 4. Exécuter ce script avec:
    echo    set GMAIL_USER=votre.email@gmail.com
    echo    set GMAIL_PASSWORD="mot-de-passe-app"
    echo    test-email.bat
    exit /b 1
)

if "%GMAIL_PASSWORD%"=="" (
    echo ERREUR: La variable GMAIL_PASSWORD n'est pas définie
    echo Voir les instructions ci-dessus
    exit /b 1
)

mvn test -Dtest=SendEmailGmailTest -DskipTests=false

if %errorlevel% equ 0 (
    echo.
    echo Email envoyé avec succès!
    echo Vérifiez votre boîte à lettres: %GMAIL_USER%
) else (
    echo.
    echo ERREUR lors de l'envoi de l'email
    echo Vérifiez:
    echo - Les identifiants Gmail corrects
    echo - La connexion internet
    echo - Les paramètres SMTP
)

pause
