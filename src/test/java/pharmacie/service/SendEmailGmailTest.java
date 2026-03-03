package pharmacie.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

/**
 * Test d'envoi d'email réel vers Gmail
 * 
 * ATTENTION: Ce test envoie un vrai email!
 * 
 * Pour utiliser ce test:
 * 1. Créer un mot de passe d'application Gmail:
 * - Aller sur https://myaccount.google.com/apppasswords
 * - Générer un mot de passe pour "Mail" et "Windows"
 * - Copier le mot de passe (16 caractères)
 * 
 * 2. Exécuter le test avec:
 * GMAIL_USER=votre.email@gmail.com GMAIL_PASSWORD="votre-mot-de-passe-app" mvn
 * test -Dtest=SendEmailGmailTest -DskipTests=false
 */
@SpringBootTest
@ActiveProfiles("test")
class SendEmailGmailTest {

    @Autowired(required = false)
    private JavaMailSender javaMailSender;

    @Autowired(required = false)
    private SendGridEmailService emailService;

    @Test
    void envoyerEmailTestVersSGmail() throws Exception {
        if (emailService == null) {
            throw new IllegalStateException("SendGridEmailService non configuré");
        }

        String recipient = "lamouscouli.labostrofa@gmail.com";
        String subject = "Test d'envoi via Spring Mail";
        String body = "Bonjour,\n\n"
                + "Ceci est un email de test envoyé via Spring Mail depuis l'application Pharmacie.\n\n"
                + "Si vous recevez cet email, la configuration est correcte.\n\n"
                + "Cordialement,\n"
                + "L'équipe Pharmacie";

        System.out.println("Envoi d'un email de test à: " + recipient);
        System.out.println("Sujet: " + subject);
        System.out.println("---");

        try {
            emailService.sendEmail(recipient, subject, body);
            System.out.println("✓ Email envoyé avec succès!");
        } catch (Exception e) {
            System.err.println("✗ Erreur lors de l'envoi:");
            e.printStackTrace();
            throw e;
        }
    }
}
