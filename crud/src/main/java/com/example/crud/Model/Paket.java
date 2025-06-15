package com.example.crud.Model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "paket")
public class Paket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String namaPaket; // Misal: Biasa, Premium

    @Column(nullable = false)
    private int kuota; // Kuota slot yang tersedia

    @Column(nullable = false)
    private int harga; // Harga paket

    @OneToMany(mappedBy = "paket", cascade = CascadeType.ALL)
    private List<User> users;

    // Getter & Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNamaPaket() {
        return namaPaket;
    }

    public void setNamaPaket(String namaPaket) {
        this.namaPaket = namaPaket;
    }

    public int getKuota() {
        return kuota;
    }

    public void setKuota(int kuota) {
        this.kuota = kuota;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
