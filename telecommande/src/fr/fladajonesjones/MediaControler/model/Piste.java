package fr.fladajonesjones.MediaControler.model;

public class Piste extends Musique {

    public String duree;
    public String albumId;
    public String getUrl(){
        return url;
    }
    public Piste(){
        
    }
    public Piste(String upnpId, String nom, String duree, String albumId, String url) {
        this.upnpId = upnpId;
        this.nom = nom;
        this.duree = duree;
        this.albumId = albumId;
        this.url = url;
    }

    @Override
    public String getMetaData() {

        return "NO METADATA";
    }

    @Override
    public long getDuration() {
        // TODO Auto-generated method stub
        return 0;
    }

}
