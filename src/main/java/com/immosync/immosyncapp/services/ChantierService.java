package com.immosync.immosyncapp.services;

import com.immosync.immosyncapp.entities.*;
import com.immosync.immosyncapp.repositories.ChantierRepository;
import com.immosync.immosyncapp.repositories.DevisTypePrestationRepository;
import com.immosync.immosyncapp.repositories.DevisTypeRepository;
import com.immosync.immosyncapp.repositories.EntrepreneurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChantierService {

    private final ChantierRepository chantierRepository;
    private final DevisTypeRepository devisTypeRepository;
    private final DevisTypePrestationRepository dtpRepository;
    private final EntrepreneurRepository entrepreneurRepository;

    public ChantierService(ChantierRepository chantierRepository,
                           DevisTypeRepository devisTypeRepository,
                           DevisTypePrestationRepository dtpRepository, EntrepreneurRepository entrepreneurRepository) {
        this.chantierRepository = chantierRepository;
        this.devisTypeRepository = devisTypeRepository;
        this.dtpRepository = dtpRepository;
        this.entrepreneurRepository = entrepreneurRepository;
    }

    public void creerChantierComplet(String description, Bien bien, Inspecteur inspecteur, Map<Prestataire, Integer> lignesPrestations) {
        Chantier chantier = new Chantier();
        chantier.setDescription(description);
        chantier.setBien(bien);
        chantier.setInspecteur(inspecteur);
        chantier.setDateCreation(Instant.now());
        chantier.setStatut("OUVERT");

        chantier = chantierRepository.save(chantier);

        DevisType devis = new DevisType();
        devis.setIntitule("Devis Type - " + bien.getAdresse());
        devis.setDateCreation(Instant.now());
        devis.setChantier(chantier);

        devis = devisTypeRepository.save(devis);

        for (Map.Entry<Prestataire, Integer> entry : lignesPrestations.entrySet()) {
            DevisTypePrestation dtp = new DevisTypePrestation();
            dtp.setDevisType(devis);
            dtp.setPrestataire(entry.getKey());
            dtp.setQuantite(entry.getValue());

            dtpRepository.save(dtp);
        }
    }
    public List<Entrepreneur> trouverEntrepreneursEligibles(Integer chantierId) {
        Chantier chantier = chantierRepository.findById(chantierId)
                .orElseThrow(() -> new RuntimeException("Chantier introuvable"));

        DevisType devis = chantier.getDevisTypes().iterator().next();

        return entrepreneurRepository.findEntrepreneursByDevisRequirements(devis.getId());
    }

    public List<Entrepreneur> trouverEntrepreneursQualifies(Chantier chantier) {
        // 1. On récupère toutes les catégories requises dans les devis types du chantier
        Set<Categorie> categoriesRequises = chantier.getDevisTypes().stream()
                .flatMap(dt -> dt.getLigneDevisTypes().stream()) // On suppose que DevisType a des lignes
                .map(LigneDevisType::getCategorie)               // Chaque ligne a une catégorie
                .collect(Collectors.toSet());

        // 2. On récupère tous les entrepreneurs
        List<Entrepreneur> tousLesEntrepreneurs = entrepreneurRepository.findAll();

        // 3. On filtre : l'entrepreneur doit posséder TOUTES les catégories requises
        return tousLesEntrepreneurs.stream()
                .filter(e -> e.getCategories().containsAll(categoriesRequises))
                .collect(Collectors.toList());
    }
}
