package pharmacie.service;

import java.io.IOException;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

@Service
public class SendGridEmailService {

    private final SendGrid sendGrid;
    private final String fromEmail;

    public SendGridEmailService(ObjectProvider<SendGrid> sendGridProvider,
            @Value("${app.email.from}") String fromEmail) {
        this.sendGrid = sendGridProvider.getIfAvailable();
        this.fromEmail = fromEmail;
    }

    SendGridEmailService(SendGrid sendGrid, String fromEmail) {
        this.sendGrid = sendGrid;
        this.fromEmail = fromEmail;
    }

    public void sendEmail(String to, String subject, String body) {
        if (sendGrid == null) {
            throw new IllegalStateException(
                    "SendGrid non configure. Definir la variable SENDGRID_API_KEY.");
        }

        Mail mail = new Mail(
                new Email(fromEmail),
                subject,
                new Email(to),
                new Content("text/plain", body));

        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");

        try {
            request.setBody(mail.build());
            Response response = sendGrid.api(request);
            if (response.getStatusCode() >= 400) {
                throw new IllegalStateException("Echec envoi email SendGrid (status="
                        + response.getStatusCode() + ")");
            }
        } catch (IOException e) {
            throw new IllegalStateException("Erreur technique lors de l'envoi d'email", e);
        }
    }
}
