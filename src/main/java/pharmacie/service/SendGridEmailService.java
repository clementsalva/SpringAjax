package pharmacie.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class SendGridEmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendGridEmailService.class);

    private final JavaMailSender javaMailSender;
    private final String fromEmail;

    @Autowired
    public SendGridEmailService(JavaMailSender javaMailSender,
            @Value("${mail.from.email}") String fromEmail) {
        this.javaMailSender = javaMailSender;
        this.fromEmail = fromEmail;
    }

    public void sendEmail(String to, String subject, String body) {
        if (!StringUtils.hasText(to) || !StringUtils.hasText(subject) || !StringUtils.hasText(body)) {
            throw new IllegalArgumentException("Les champs to, subject et body sont obligatoires.");
        }

        if (!StringUtils.hasText(fromEmail)) {
            throw new IllegalStateException("Mail non configure. Definir la propriete mail.from.email.");
        }

        if (javaMailSender == null) {
            throw new IllegalStateException("JavaMailSender non configure. Verifier la configuration Spring Mail.");
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            javaMailSender.send(message);
            LOGGER.info("Email envoyé avec succès à {}", to);
        } catch (Exception e) {
            LOGGER.error("Erreur technique lors de l'envoi d'email", e);
            throw new IllegalStateException("Erreur technique lors de l'envoi d'email", e);
        }
    }
}
