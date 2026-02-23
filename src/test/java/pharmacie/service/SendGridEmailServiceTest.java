package pharmacie.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;

@ExtendWith(MockitoExtension.class)
class SendGridEmailServiceTest {

    @Mock
    private SendGrid sendGrid;

    private SendGridEmailService service;

    @BeforeEach
    void setUp() {
        service = new SendGridEmailService(sendGrid, "from@test.local");
    }

    @Test
    void sendEmailAppelleApiSendGrid() throws IOException {
        when(sendGrid.api(any(Request.class))).thenReturn(new Response(202, "", new HashMap<>()));

        service.sendEmail("to@test.local", "Sujet", "Contenu");

        verify(sendGrid).api(any(Request.class));
    }

    @Test
    void sendEmailLeveUneExceptionSiSendGridRepondAvecErreur() throws IOException {
        when(sendGrid.api(any(Request.class))).thenReturn(new Response(500, "", new HashMap<>()));

        assertThrows(IllegalStateException.class,
                () -> service.sendEmail("to@test.local", "Sujet", "Contenu"));
    }

    @Test
    void sendEmailLeveUneExceptionSiErreurTechnique() throws IOException {
        when(sendGrid.api(any(Request.class))).thenThrow(new IOException("network"));

        assertThrows(IllegalStateException.class,
                () -> service.sendEmail("to@test.local", "Sujet", "Contenu"));
    }
}
