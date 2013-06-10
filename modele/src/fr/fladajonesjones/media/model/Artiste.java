package fr.fladajonesjones.media.model;

import java.util.List;

public class Artiste implements Comparable<Artiste> {
    private int id;

    public List<Album> getLstAlbum() {
        return lstAlbum;
    }

    public void setLstAlbum(List<Album> lstAlbum) {
        this.lstAlbum = lstAlbum;
    }

    private List<Album> lstAlbum;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getNbAlbum() {
        return nbAlbum;
    }

    public void setNbAlbum(int nbAlbum) {
        this.nbAlbum = nbAlbum;
    }

    private String nom;
    private int nbAlbum;

    public Artiste(int id, String nom, int nbAlbum) {
        this.id = id;
        this.nom = nom;
        this.nbAlbum = nbAlbum;
    }

    @Override
    public int compareTo(Artiste arg0) {
        return this.nom.compareTo(arg0.nom);

    }
}
