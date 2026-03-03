package pharmacie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

import pharmacie.service.SendGridEmailService;

/**
 * Classe pour tester l'envoi d'emails via Spring Mail
 * Pour l'utiliser:
 * mvn test -Dtest=pharmacie.EmailTestRunner -DskipTests=false
 * 
 * Ou avec un profil spécifique:
 * mvn spring-boot:run -Dspring-boot.run.profiles=test
 * -Dspring-boot.run.arguments="--test.mail.recipient=lamouscouli.labostrofa@gmail.com"
 */
@SpringBootApplication
@Profile("test")
public class EmailTestRunner implements CommandLineRunner {

    @Autowired
    private SendGridEmailService emailService;

    @Override
    public void run(String... args) throws Exception {
        String recipient = "lamouscouli.labostrofa@gmail.com";
        String subject = "Test Spring Mail";
        String body = "Bonjour,\n\nCeci est un email de test envoyé via Spring Mail.\n\nCordialement,\nL'équipe Pharmacie";

        System.out.println("Envoi d'un email de test à: " + recipient);
        try {
            emailService.sendEmail(recipient, subject, body);
            System.out.println("✓ Email envoyé avec succès!");
        } catch (Exception e) {
            System.err.println("✗ Erreur lors de l'envoi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(EmailTestRunner.class);
        app.setAdditionalProfiles("test");
        app.run(args);
    }
}
