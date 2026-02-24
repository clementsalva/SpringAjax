package pharmacie.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import pharmacie.dao.MedicamentRepository;
import pharmacie.dto.MedicamentAReapprovisionnerDTO;
import pharmacie.entity.Medicament;

@Service
public class ApprovisionnementService {

    private final MedicamentRepository medicamentRepository;
    private final SendGridEmailService sendGridEmailService;
    private final String approvisionnementRecipient;

    public ApprovisionnementService(
            MedicamentRepository medicamentRepository,
            SendGridEmailService sendGridEmailService,
            @Value("${approvisionnement.mail.to:${sendgrid.from.email:}}") String approvisionnementRecipient) {
        this.medicamentRepository = medicamentRepository;
        this.sendGridEmailService = sendGridEmailService;
        this.approvisionnementRecipient = approvisionnementRecipient;
    }

    @Transactional(readOnly = true)
    public List<MedicamentAReapprovisionnerDTO> medicamentsAReapprovisionner() {
        return medicamentRepository.medicamentsAReapprovisionner()
                .stream()
                .sorted(Comparator.comparing((Medicament m) -> m.getCategorie().getLibelle())
                        .thenComparing(Medicament::getNom))
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ResultatApprovisionnement lancerApprovisionnement() {
        List<MedicamentAReapprovisionnerDTO> medicaments = medicamentsAReapprovisionner();
        if (medicaments.isEmpty()) {
            return new ResultatApprovisionnement("Aucun medicament a reapprovisionner", 0, 0);
        }

        if (!StringUtils.hasText(approvisionnementRecipient)) {
            throw new IllegalStateException(
                    "Destinataire approvisionnement non configure. Definir approvisionnement.mail.to.");
        }

        Map<String, List<MedicamentAReapprovisionnerDTO>> parCategorie = medicaments.stream()
                .collect(Collectors.groupingBy(MedicamentAReapprovisionnerDTO::categorie));

        int emailsEnvoyes = 0;
        for (Map.Entry<String, List<MedicamentAReapprovisionnerDTO>> entry : parCategorie.entrySet()) {
            String categorie = entry.getKey();
            List<MedicamentAReapprovisionnerDTO> liste = entry.getValue();

            String sujet = "Demande de devis reapprovisionnement - " + categorie;
            String corps = construireCorpsEmail(categorie, liste);
            sendGridEmailService.sendEmail(approvisionnementRecipient, sujet, corps);
            emailsEnvoyes++;
        }

        return new ResultatApprovisionnement(
                "Reapprovisionnement lance. Emails envoyes a " + approvisionnementRecipient,
                medicaments.size(),
                emailsEnvoyes);
    }

    private MedicamentAReapprovisionnerDTO toDto(Medicament medicament) {
        int quantiteACommander = Math.max(0,
                (medicament.getNiveauDeReappro() * 2) - (medicament.getUnitesEnStock() - medicament.getUnitesCommandees()));
        return new MedicamentAReapprovisionnerDTO(
                medicament.getReference(),
                medicament.getNom(),
                medicament.getCategorie().getLibelle(),
                medicament.getUnitesEnStock(),
                medicament.getUnitesCommandees(),
                medicament.getNiveauDeReappro(),
                quantiteACommander);
    }

    private String construireCorpsEmail(String categorie, List<MedicamentAReapprovisionnerDTO> medicaments) {
        StringBuilder sb = new StringBuilder();
        sb.append("Bonjour,\n\n");
        sb.append("Merci de nous transmettre un devis pour la categorie: ").append(categorie).append(".\n\n");
        sb.append("Medicaments a reapprovisionner:\n");
        for (MedicamentAReapprovisionnerDTO m : medicaments) {
            sb.append("- ").append(m.nom())
                    .append(" (stock=").append(m.unitesEnStock())
                    .append(", commandes=").append(m.unitesCommandees())
                    .append(", seuil=").append(m.niveauDeReappro())
                    .append(", quantite demandee=").append(m.quantiteACommander())
                    .append(")\n");
        }
        sb.append("\nCordialement,\nService Approvisionnement");
        return sb.toString();
    }

    public record ResultatApprovisionnement(String message, int nbMedicaments, int nbEmails) {
    }
}
