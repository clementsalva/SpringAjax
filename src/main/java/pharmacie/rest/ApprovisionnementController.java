package pharmacie.rest;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pharmacie.service.ApprovisionnementService;

@RestController
@RequestMapping("/api/approvisionnement")
public class ApprovisionnementController {

    private final ApprovisionnementService approvisionnementService;

    public ApprovisionnementController(ApprovisionnementService approvisionnementService) {
        this.approvisionnementService = approvisionnementService;
    }

    @GetMapping("/medicaments")
    public ResponseEntity<?> medicamentsAReapprovisionner() {
        return ResponseEntity.ok(approvisionnementService.medicamentsAReapprovisionner());
    }

    @PostMapping("/lancer")
    public ResponseEntity<Map<String, Object>> lancerApprovisionnement() {
        ApprovisionnementService.ResultatApprovisionnement resultat = approvisionnementService.lancerApprovisionnement();
        return ResponseEntity.ok(Map.of(
                "message", resultat.message(),
                "nbMedicaments", resultat.nbMedicaments(),
                "nbEmails", resultat.nbEmails()));
    }
}
