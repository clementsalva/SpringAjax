package pharmacie.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import pharmacie.dao.MedicamentRepository;
import pharmacie.entity.Medicament;

@Service
public class MedicamentService {

    private final MedicamentRepository medicamentRepository;
    private final SendGridEmailService sendGridEmailService;
    private final String stockAlertRecipient;

    public MedicamentService(
            MedicamentRepository medicamentRepository,
            SendGridEmailService sendGridEmailService,
            @Value("${stock.alert.mail.to:${sendgrid.from.email:}}") String stockAlertRecipient) {
        this.medicamentRepository = medicamentRepository;
        this.sendGridEmailService = sendGridEmailService;
        this.stockAlertRecipient = stockAlertRecipient;
    }

    @Transactional(readOnly = true)
    public List<Medicament> getTousLesMedicaments() {
        return medicamentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Medicament> medicamentsStockBas() {
        return medicamentRepository.medicamentsStockBas();
    }

    @Transactional
    public ResultatAlertStock lancerAlerteStockBas() {
        List<Medicament> medicaments = medicamentsStockBas();

        if (medicaments.isEmpty()) {
            return new ResultatAlertStock("Aucun medicament en stock bas", 0);
        }

        if (!StringUtils.hasText(stockAlertRecipient)) {
            throw new IllegalStateException(
                    "Destinataire alerte stock non configure. Definir stock.alert.mail.to.");
        }

        int emailsEnvoyes = 0;
        for (Medicament medicament : medicaments) {
            String sujet = "Alerte Stock Bas - " + medicament.getNom();
            String corps = "Attention il ne reste plus beaucoup de " + medicament.getNom()
                    + " en stock veuillez passer commande";

            sendGridEmailService.sendEmail(stockAlertRecipient, sujet, corps);
            emailsEnvoyes++;
        }

        return new ResultatAlertStock(
                "Alerte stock lancee. " + emailsEnvoyes + " email(s) envoye(s) a " + stockAlertRecipient,
                emailsEnvoyes);
    }

    public record ResultatAlertStock(String message, int nbEmails) {
    }
}
