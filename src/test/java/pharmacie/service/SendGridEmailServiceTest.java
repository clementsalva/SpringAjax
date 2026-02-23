package pharmacie.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sendgrid.Request;
import com.sendgrid.Response;

@ExtendWith(MockitoExtension.class)
class SendGridEmailServiceTest {

    @Mock
    private SendGridEmailService.SendGridGateway sendGridGateway;

    private SendGridEmailService service;

    @BeforeEach
    void setUp() {
        service = new SendGridEmailService(sendGridGateway, "from@test.local");
    }

    @Test
    void sendEmailAppelleApiSendGrid() throws IOException {
        when(sendGridGateway.api(any(Request.class)))
                .thenReturn(new Response(202, "", Collections.emptyMap()));

        service.sendEmail("to@test.local", "Sujet", "Contenu");

        verify(sendGridGateway).api(any(Request.class));
    }

    @Test
    void sendEmailLeveUneExceptionSiSendGridRetourneUneErreurMetier() throws IOException {
        when(sendGridGateway.api(any(Request.class)))
                .thenReturn(new Response(400, "bad request", Collections.emptyMap()));

        assertThrows(IllegalStateException.class,
                () -> service.sendEmail("to@test.local", "Sujet", "Contenu"));
    }

    @Test
    void sendEmailLeveUneExceptionSiErreurTechnique() throws IOException {
        when(sendGridGateway.api(any(Request.class))).thenThrow(new IOException("network"));

        assertThrows(IllegalStateException.class,
                () -> service.sendEmail("to@test.local", "Sujet", "Contenu"));
    }

    @Test
    void sendEmailLeveUneExceptionSiClientNonConfigure() {
        SendGridEmailService unconfiguredService = new SendGridEmailService((SendGridEmailService.SendGridGateway) null,
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
