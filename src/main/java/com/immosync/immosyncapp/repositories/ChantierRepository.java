package com.immosync.immosyncapp.repositories;

import com.immosync.immosyncapp.entities.Chantier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ChantierRepository extends JpaRepository<Chantier, Integer> {
}
