package pharmacie.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pharmacie.dao.MedicamentRepository;

@SpringBootTest
class MedicamentServiceTest {

    @Autowired
    private MedicamentService medicamentService;

    @Autowired
    private MedicamentRepository medicamentRepository;

    @Test
    void getTousLesMedicamentsRetourneTousLesMedicaments() {
        var medicaments = medicamentService.getTousLesMedicaments();

        assertEquals(medicamentRepository.count(), medicaments.size());
        assertTrue(medicaments.size() > 1, "Le jeu de test doit contenir plusieurs medicaments");
    }
}
