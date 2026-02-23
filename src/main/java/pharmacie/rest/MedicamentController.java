package pharmacie.rest;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import pharmacie.entity.Medicament;
import pharmacie.service.MedicamentService;

@RestController
public class MedicamentController {

    private final MedicamentService medicamentService;

    public MedicamentController(MedicamentService medicamentService) {
        this.medicamentService = medicamentService;
    }

    @GetMapping("/medicaments")
    public List<Medicament> getTousLesMedicaments() {
        return medicamentService.getTousLesMedicaments();
    }
}
