package fr.flagadajones.mediarenderer;

public class AudioItem {

    public String albumArt;
    public     String artiste;
    public String duration= "00:00:00";
    public String title;
    public String url;
    
    public String toString(){
    	return title+'\n'+duration;
    }
}
