package pharmacie.rest;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import pharmacie.dto.MedicamentAReapprovisionnerDTO;
import pharmacie.service.ApprovisionnementService;

@SpringBootTest
@AutoConfigureMockMvc
class ApprovisionnementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApprovisionnementService approvisionnementService;

    @Test
    void getMedicamentsRetourneLaListe() throws Exception {
        when(approvisionnementService.medicamentsAReapprovisionner()).thenReturn(List.of(
                new MedicamentAReapprovisionnerDTO(
                        1,
                        "Amoxicilline",
                        "Antibiotiques",
                        2,
                        0,
                        10,
                        18)));

        mockMvc.perform(get("/api/approvisionnement/medicaments")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom").value("Amoxicilline"))
                .andExpect(jsonPath("$[0].categorie").value("Antibiotiques"));
    }

    @Test
    void postLancerRetourneLeResultat() throws Exception {
        when(approvisionnementService.lancerApprovisionnement())
                .thenReturn(new ApprovisionnementService.ResultatApprovisionnement(
                        "Reapprovisionnement lance",
                        3,
                        2));

        mockMvc.perform(post("/api/approvisionnement/lancer")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Reapprovisionnement lance"))
                .andExpect(jsonPath("$.nbMedicaments").value(3))
                .andExpect(jsonPath("$.nbEmails").value(2));

        verify(approvisionnementService).lancerApprovisionnement();
    }
}
