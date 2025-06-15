package com.example.crud.service;

import com.example.crud.Model.Cinema;
import com.example.crud.repository.CinemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CinemaService {

    @Autowired
    private CinemaRepository cinemaRepository;

    // Sudah ada — biarkan
    public List<Cinema> getAllCinemas() {
        return cinemaRepository.findAll();
    }

    // Tambahan — Ambil cinema by ID
    public Cinema getCinemaById(Long id) {
        Optional<Cinema> optionalCinema = cinemaRepository.findById(id);
        return optionalCinema.orElse(null); // atau lempar exception sesuai preferensi
    }

    // Tambahan — Simpan cinema (baru atau update)
    public void saveCinema(Cinema cinema) {
        cinemaRepository.save(cinema);
    }

    // Tambahan — Hapus cinema by ID
    public void deleteCinema(Long id) {
        cinemaRepository.deleteById(id);
    }
}
