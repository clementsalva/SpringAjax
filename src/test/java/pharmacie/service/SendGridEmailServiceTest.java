package pharmacie.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class SendGridEmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    private SendGridEmailService service;

    @BeforeEach
    void setUp() {
        service = new SendGridEmailService(javaMailSender, "from@test.local");
    }

    @Test
    void sendEmailEnvoieUnEmail() {
        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        service.sendEmail("to@test.local", "Sujet", "Contenu");

        verify(javaMailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEmailLeveUneExceptionSiErreurTechnique() {
        doThrow(new MailException("network error") {
        })
                .when(javaMailSender).send(any(SimpleMailMessage.class));

        assertThrows(IllegalStateException.class,
                () -> service.sendEmail("to@test.local", "Sujet", "Contenu"));
    }

    @Test
    void sendEmailLeveUneExceptionSiClientNonConfigure() {
        SendGridEmailService unconfiguredService = new SendGridEmailService(null,
                "from@test.local");

        assertThrows(IllegalStateException.class,
                () -> unconfiguredService.sendEmail("to@test.local", "Sujet", "Contenu"));
    }

    @Test
    void sendEmailLeveUneExceptionSiChampsManquants() {
        assertThrows(IllegalArgumentException.class,
                () -> service.sendEmail("", "Sujet", "Contenu"));
    }
}
