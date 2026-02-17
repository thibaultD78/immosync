package com.immosync.immosyncapp.repositories;

import com.immosync.immosyncapp.entities.Entrepreneur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntrepreneurRepository extends JpaRepository<Entrepreneur, Integer> {

    @Query("""
        SELECT e FROM Entrepreneur e 
        WHERE NOT EXISTS (
            SELECT DISTINCT p.categorie FROM DevisTypePrestation dtp 
            JOIN dtp.prestataire p 
            WHERE dtp.devisType.id = :devisId
            AND p.categorie NOT MEMBER OF e.categories 
        )
    """)
    List<Entrepreneur> findEntrepreneursByDevisRequirements(@Param("devisId") Integer devisId);
}
