package com.example.crud.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Mahasiswa {

    @Id
    private String nrp;

    private String name;

    public Mahasiswa() {}

    public Mahasiswa(String nrp, String name) {
        this.nrp = nrp;
        this.name = name;
    }

    public String getNrp() {
        return nrp;
    }

    public void setNrp(String nrp) {
        this.nrp = nrp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
