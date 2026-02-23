package pharmacie.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pharmacie.dao.MedicamentRepository;
import pharmacie.entity.Medicament;

@Service
public class MedicamentService {

    private final MedicamentRepository medicamentRepository;

    public MedicamentService(MedicamentRepository medicamentRepository) {
        this.medicamentRepository = medicamentRepository;
    }

    @Transactional(readOnly = true)
    public List<Medicament> getTousLesMedicaments() {
        return medicamentRepository.findAll();
    }
}
