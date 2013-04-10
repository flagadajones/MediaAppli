package fr.fladajonesjones.MediaControler.model;

public abstract class Musique {
public String upnpId;
    public String nom;
    protected String metaData;
    public String icone;
    public String url;
    
    
   public abstract String getUrl();
    public abstract String getMetaData();
    public abstract long getDuration();
    
}
