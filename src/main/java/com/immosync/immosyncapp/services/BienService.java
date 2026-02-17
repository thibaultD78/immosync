package com.immosync.immosyncapp.services;

import com.immosync.immosyncapp.entities.Bien;
import com.immosync.immosyncapp.entities.Utilisateur;
import com.immosync.immosyncapp.repositories.BienRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BienService {

    private final BienRepository bienRepository;

    public BienService(BienRepository bienRepository) {
        this.bienRepository = bienRepository;
    }

    public List<Bien> getAllBiens() {
        return bienRepository.findAll();
    }

    public List<Bien> getBiensUtilisateur(Utilisateur utilisateur) {
        return bienRepository.findByUtilisateur(utilisateur);
    }
    public Bien creerBien(String adresse, String ville, String cp, BigDecimal surface, Utilisateur proprietaire) {
        Bien nouveauBien = new Bien();
        nouveauBien.setAdresse(adresse);
        nouveauBien.setVille(ville);
        nouveauBien.setCodePostal(cp);
        nouveauBien.setSurface(surface);
        nouveauBien.setUtilisateur(proprietaire);

        return bienRepository.save(nouveauBien);
    }
    public Bien modifierBien(Integer id, String adresse, String ville, String cp, BigDecimal surface) {
        Bien bienExistant = bienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bien introuvable avec l'ID : " + id));

        bienExistant.setAdresse(adresse);
        bienExistant.setVille(ville);
        bienExistant.setCodePostal(cp);
        bienExistant.setSurface(surface);

        return bienRepository.save(bienExistant);
    }
}
