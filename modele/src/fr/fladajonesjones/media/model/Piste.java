package fr.fladajonesjones.media.model;

public class Piste extends Musique {
    public String artiste;
    public String duree = "00:00:00";
    public String albumId;

    public String getUrl() {
        return url;
    }

    public Piste() {

    }

    public Piste(String upnpId, String nom, String duree, String albumId, String url) {
        this.upnpId = upnpId;
        this.titre = nom;
        this.duree = duree;
        this.albumId = albumId;
        this.url = url;
    }

    @Override
    public String getDuree() {

        return duree;
    }


}
