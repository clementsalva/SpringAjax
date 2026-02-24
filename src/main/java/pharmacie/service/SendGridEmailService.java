package pharmacie.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

@Service
public class SendGridEmailService {

    @FunctionalInterface
    interface SendGridGateway {
        Response api(Request request) throws IOException;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SendGridEmailService.class);

    private final SendGridGateway sendGridGateway;
    private final String sendGridApiKey;
    private final String fromEmail;

    @Autowired
    public SendGridEmailService(@Value("${sendgrid.api.key:}") String sendGridApiKey,
            @Value("${sendgrid.from.email}") String fromEmail) {
        this.sendGridGateway = null;
        this.sendGridApiKey = sendGridApiKey;
        this.fromEmail = fromEmail;
    }

    SendGridEmailService(SendGridGateway sendGridGateway, String fromEmail) {
        this.sendGridGateway = sendGridGateway;
        this.sendGridApiKey = null;
        this.fromEmail = fromEmail;
    }

    public void sendEmail(String to, String subject, String body) {
        if (!StringUtils.hasText(to) || !StringUtils.hasText(subject) || !StringUtils.hasText(body)) {
            throw new IllegalArgumentException("Les champs to, subject et body sont obligatoires.");
        }

        if (!StringUtils.hasText(fromEmail)) {
            throw new IllegalStateException("SendGrid non configure. Definir la propriete sendgrid.from.email.");
        }

        SendGridGateway gateway = resolveGateway();
        if (gateway == null) {
            throw new IllegalStateException("SendGrid non configure. Definir la propriete sendgrid.api.key.");
        }

        Mail mail = new Mail(new Email(fromEmail), subject, new Email(to), new Content("text/plain", body));

        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");

        try {
            request.setBody(mail.build());
            Response response = gateway.api(request);
            int statusCode = response.getStatusCode();

            LOGGER.info("SendGrid response status={} body={}", statusCode, response.getBody());

            if (statusCode < 200 || statusCode >= 300) {
                String details = response.getBody() == null ? "" : " - " + response.getBody();
                throw new IllegalStateException("Echec envoi email SendGrid: HTTP " + statusCode + details);
            }
        } catch (IOException e) {
            LOGGER.error("Erreur technique lors de l'envoi SendGrid", e);
            throw new IllegalStateException("Erreur technique lors de l'envoi d'email", e);
        }
    }

    private SendGridGateway resolveGateway() {
        if (sendGridGateway != null) {
            return sendGridGateway;
        }
        if (!StringUtils.hasText(sendGridApiKey)) {
            return null;
        }

        SendGrid client = new SendGrid(sendGridApiKey);
        return client::api;
    }
}
