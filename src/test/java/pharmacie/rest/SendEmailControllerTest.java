package pharmacie.rest;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import pharmacie.service.SendGridEmailService;

@WebMvcTest(SendEmailController.class)
class SendEmailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SendGridEmailService sendGridEmailService;

    @Test
    void sendEmailRetourne200() throws Exception {
        String payload = """
                {
                  "to": "dest@test.local",
                  "subject": "Demande devis",
                  "body": "Bonjour"
                }
                """;

        mockMvc.perform(post("/send-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Email envoye"));

        verify(sendGridEmailService).sendEmail("dest@test.local", "Demande devis", "Bonjour");
    }

    @Test
    void sendEmailRetourne400SiServiceEnErreurMetier() throws Exception {
        doThrow(new IllegalStateException("Echec envoi email SendGrid"))
                .when(sendGridEmailService)
                .sendEmail("dest@test.local", "Demande devis", "Bonjour");

        String payload = """
                {
                  "to": "dest@test.local",
                  "subject": "Demande devis",
                  "body": "Bonjour"
                }
                """;

        mockMvc.perform(post("/send-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Illegal state error"));
    }
}
