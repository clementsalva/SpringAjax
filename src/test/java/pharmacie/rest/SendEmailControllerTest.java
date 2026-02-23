package pharmacie.rest;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import pharmacie.service.SendGridEmailService;

@SpringBootTest
@AutoConfigureMockMvc
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

        mockMvc.perform(post("/mail/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Mail envoye a dest@test.local"));

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

        mockMvc.perform(post("/mail/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Illegal state error"));
    }

    @Test
    void sendEmailAccepteLesQueryParams() throws Exception {
        mockMvc.perform(post("/mail/send")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("to", "query@test.local")
                        .param("subject", "Sujet Query")
                        .param("body", "Bonjour query"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Mail envoye a query@test.local"));

        verify(sendGridEmailService).sendEmail("query@test.local", "Sujet Query", "Bonjour query");
    }
}
