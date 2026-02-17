package com.immosync.immosyncapp.repositories;

import com.immosync.immosyncapp.entities.Bien;
import com.immosync.immosyncapp.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BienRepository extends JpaRepository<Bien, Integer> {

    List<Bien> findByUtilisateur(Utilisateur utilisateur);

    List<Bien> findByUtilisateurId(Integer utilisateurId);


}
