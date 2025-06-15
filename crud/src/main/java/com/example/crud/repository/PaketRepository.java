package com.example.crud.repository;

import com.example.crud.Model.Paket;
import org.springframework.data.jpa.repository.JpaRepository;

// Interface ini mewarisi JpaRepository
public interface PaketRepository extends JpaRepository<Paket, Long> {
    
}
