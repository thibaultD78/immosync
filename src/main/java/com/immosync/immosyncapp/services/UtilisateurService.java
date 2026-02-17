package com.immosync.immosyncapp.services;

import com.immosync.immosyncapp.entities.Utilisateur;
import com.immosync.immosyncapp.repositories.UtilisateurRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UtilisateurService {

    private final UtilisateurRepository userRepository;

    public UtilisateurService(UtilisateurRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Utilisateur existeUser(String loginUser, String pwdUser) {
        return userRepository.findByEmailAndPassword(loginUser, pwdUser);
    }

    /**
     * Récupère tous les utilisateurs
     */
    public List<Utilisateur> getAllUsers() {
        return userRepository.findAll();
    }
}
