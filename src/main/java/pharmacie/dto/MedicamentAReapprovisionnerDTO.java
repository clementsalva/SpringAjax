package pharmacie.dto;

public record MedicamentAReapprovisionnerDTO(
        Integer reference,
        String nom,
        String categorie,
        int unitesEnStock,
        int unitesCommandees,
        int niveauDeReappro,
        int quantiteACommander) {
}
