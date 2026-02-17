package com.immosync.immosyncapp.repositories;

import com.immosync.immosyncapp.entities.DevisType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface DevisTypeRepository extends JpaRepository<DevisType, Integer> {
}
