package fr.fladajonesjones.MediaControler.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.fourthline.cling.support.contentdirectory.DIDLParser;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.container.MusicAlbum;
import org.fourthline.cling.support.model.item.MusicTrack;
import org.seamless.util.MimeType;

import android.os.Parcel;
import android.os.Parcelable;
import fr.fladajonesjones.MediaControler.database.PisteDAO;

public class Album extends Musique implements Parcelable, Comparable<Album> {

    public int nbTracks;
    public int artisteId;
    public int order;

    private List<Piste> lstPistes = null;

    public Artiste artiste;

    public Album(String upnpId, String nom, int nbTracks, int artisteId, String albumArt) {

        this.upnpId = upnpId;
        this.nom = nom;
        this.nbTracks = nbTracks;
        this.artisteId = artisteId;
        this.icone = albumArt;

    }
    
    public boolean isPisteLoaded(){
    	if(lstPistes==null || lstPistes.size()!=nbTracks){
    		return false;
    	}
    	return true;
    }

    public String getUrl() {

//try {
//	playlist.createNewFile();
//} catch (IOException e1) {
//	// TODO Auto-generated catch block
//	e1.printStackTrace();
//}
        
        String adresse="127.0.0.1";
        String port ="80";

        String url = null;

        url = "http://" + adresse+ ":" + port + "/" + upnpId + ".m3u";
        
        return url;
    }

    public List<Piste> getPistes() {
        if (!isPisteLoaded()) {
            PisteDAO pisteDAO = new PisteDAO();
            lstPistes = pisteDAO.getAllPistes(upnpId);
        }

        return lstPistes;
    }

    public String getMetaData() { 
       // metaData = "NO METADATA"; 
    //    if (metaData == null) { 
        DIDLContent didl = new DIDLContent(); 

        MusicAlbum item = new MusicAlbum(); 
        // item.setRadioBand(radio.nom); 
        item.setTitle(nom); 
        item.setId(nom); 
        item.setParentID("0"); 
        try {
        	if (this.icone!=null)
        	item.addProperty(new DIDLObject.Property.UPNP.ALBUM_ART_URI(new URI(this.icone)));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        
        // item.setRadioCallSign(radio.nom); 
List<MusicTrack> list = new ArrayList<MusicTrack>();


        for (Piste iterable_element : getPistes()) { 
            MusicTrack piste=new MusicTrack(); 
            piste.setAlbum(nom); 
            piste.setId(iterable_element.upnpId);
            piste.setParentID(iterable_element.albumId);
            piste.setRestricted(false);
            piste.setTitle(iterable_element.nom);
            try {
            	if(iterable_element.icone!=null)
				item.addProperty(new DIDLObject.Property.UPNP.ALBUM_ART_URI(new URI(iterable_element.icone)));
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
            piste.addResource(new Res(MimeType.valueOf("audio/mp3"), null, iterable_element.url)); 
            list.add(piste); 
        } 
        item.addMusicTracks(list);
        didl.addContainer(item); 

        try { 
            item.addProperty(new DIDLObject.Property.UPNP.ALBUM_ART_URI(new URI(icone))); 
            metaData = new DIDLParser().generate(didl,true); 
        } catch (Exception e1) { 
            // Toast.makeText(Application.instance, e1.getMessage(), Toast.LENGTH_LONG).show(); 
            metaData = "NO METADATA"; 

        } 
    //} 

    return metaData; 
}

    public long getDuration() {
        return 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nom);
        dest.writeInt(artisteId);
        dest.writeString(url);
        dest.writeString(icone);
        dest.writeInt(nbTracks);

    }

    public static final Parcelable.Creator<Album> CREATOR = new Parcelable.Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel source) {
            return new Album(source);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    public Album() {

    }

    public Album(Parcel in) {
        this.nom = in.readString();
        this.artisteId = in.readInt();
        this.url = in.readString();
        this.icone = in.readString();
        this.nbTracks = in.readInt();

    }

    @Override
    public int compareTo(Album arg0) {
        if(this.artiste.getNom().equals(arg0.artiste.getNom()))
        	return this.nom.compareTo(arg0.nom);
        	else{
        	return  this.artiste.getNom().compareTo(arg0.artiste.getNom());
        	}

    }
}
