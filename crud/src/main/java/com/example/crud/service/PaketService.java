package com.example.crud.service;

import com.example.crud.Model.Paket;
import com.example.crud.repository.PaketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaketService {

    @Autowired
    private PaketRepository paketRepository;

    // Ambil semua paket
    public List<Paket> getAllPakets() {
        return paketRepository.findAll();
    }

    // Ambil paket berdasarkan ID
    public Paket getPaketById(Long id) {
        Optional<Paket> optionalPaket = paketRepository.findById(id);
        return optionalPaket.orElse(null); // Bisa lempar exception jika mau
    }

    // Simpan paket baru atau update
    public void savePaket(Paket paket) {
        paketRepository.save(paket);
    }

    // Hapus paket berdasarkan ID
    public void deletePaket(Long id) {
        paketRepository.deleteById(id);
    }
}
