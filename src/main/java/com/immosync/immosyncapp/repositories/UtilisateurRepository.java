package com.immosync.immosyncapp.repositories;

import com.immosync.immosyncapp.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {

    Utilisateur findByEmailAndPassword(String loginUser, String pwdUser);

    @Override
    List<Utilisateur> findAll();

    @Override
    Optional<Utilisateur> findById(Integer id);
}
