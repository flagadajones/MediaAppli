package fr.fladajonesjones.media.model;

public abstract class Musique {
public String upnpId;
    public String titre;
    protected String metaData;
    public String albumArt;
    public String url;
    

    
   public abstract String getUrl();
    public abstract String getDuree();
    
    
//    public String getDureeAsString(){
//        return "00:00:00";
//    }
    public String toString(){
        return titre+'\n'+getDuree();
    }
}
