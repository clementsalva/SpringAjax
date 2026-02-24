package pharmacie.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pharmacie.dao.MedicamentRepository;
import pharmacie.entity.Categorie;
import pharmacie.entity.Medicament;

@ExtendWith(MockitoExtension.class)
class ApprovisionnementServiceTest {

    @Mock
    private MedicamentRepository medicamentRepository;

    @Mock
    private SendGridEmailService sendGridEmailService;

    private ApprovisionnementService approvisionnementService;

    @BeforeEach
    void setUp() {
        approvisionnementService = new ApprovisionnementService(
                medicamentRepository,
                sendGridEmailService,
                "dest@test.local");
    }

    @Test
    void medicamentsAReapprovisionnerRetourneLesMedicamentsFiltres() {
        Categorie cat = new Categorie("Antibiotiques");
        Medicament m = new Medicament("Amoxicilline", cat);
        m.setUnitesEnStock(3);
        m.setUnitesCommandees(1);
        m.setNiveauDeReappro(8);

        when(medicamentRepository.medicamentsAReapprovisionner()).thenReturn(List.of(m));

        var result = approvisionnementService.medicamentsAReapprovisionner();

        assertEquals(1, result.size());
        assertEquals("Amoxicilline", result.getFirst().nom());
        assertEquals("Antibiotiques", result.getFirst().categorie());
    }

    @Test
    void lancerApprovisionnementEnvoieUnEmailParCategorie() {
        Categorie cat1 = new Categorie("Antibiotiques");
        Medicament m1 = new Medicament("Amoxicilline", cat1);
        m1.setUnitesEnStock(2);
        m1.setUnitesCommandees(0);
        m1.setNiveauDeReappro(10);

        Categorie cat2 = new Categorie("Antalgiques");
        Medicament m2 = new Medicament("Paracetamol", cat2);
        m2.setUnitesEnStock(1);
        m2.setUnitesCommandees(0);
        m2.setNiveauDeReappro(10);

        when(medicamentRepository.medicamentsAReapprovisionner()).thenReturn(List.of(m1, m2));

        var result = approvisionnementService.lancerApprovisionnement();

        assertEquals(2, result.nbMedicaments());
        assertEquals(2, result.nbEmails());
        verify(sendGridEmailService, times(2)).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void lancerApprovisionnementSansMedicamentNenvoieAucunEmail() {
        when(medicamentRepository.medicamentsAReapprovisionner()).thenReturn(List.of());

        var result = approvisionnementService.lancerApprovisionnement();

        assertEquals(0, result.nbMedicaments());
        assertEquals(0, result.nbEmails());
        verify(sendGridEmailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void lancerApprovisionnementEchoueSiDestinataireNonConfigure() {
        ApprovisionnementService serviceSansDest = new ApprovisionnementService(
                medicamentRepository,
                sendGridEmailService,
                "");

        Categorie cat = new Categorie("Antibiotiques");
        Medicament m = new Medicament("Amoxicilline", cat);
        m.setUnitesEnStock(2);
        m.setUnitesCommandees(0);
        m.setNiveauDeReappro(10);

        when(medicamentRepository.medicamentsAReapprovisionner()).thenReturn(List.of(m));

        assertThrows(IllegalStateException.class, serviceSansDest::lancerApprovisionnement);
    }
}
