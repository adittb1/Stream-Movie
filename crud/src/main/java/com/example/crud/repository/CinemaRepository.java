package com.example.crud.repository;

import com.example.crud.Model.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// Interface ini mewarisi JpaRepository
public interface CinemaRepository extends JpaRepository<Cinema, Long> {
}
