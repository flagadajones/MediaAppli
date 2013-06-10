package fr.fladajonesjones.media.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Album extends Musique implements Parcelable, Comparable<Album> {

    public int nbTracks;
    public int artisteId;
    public int order;

    private List<Piste> lstPistes = null;

    public Artiste artiste;

    public void addPiste(Piste piste) {
        if (lstPistes == null)
            lstPistes = new ArrayList<Piste>();
        lstPistes.add(piste);
    }

    public Album(String upnpId, String nom, int nbTracks, int artisteId, String albumArt) {

        this.upnpId = upnpId;
        this.titre = nom;
        this.nbTracks = nbTracks;
        this.artisteId = artisteId;
        this.albumArt = albumArt;

    }


    public String getDuree() {
        return "00:00:00";
    }

    public boolean isPisteLoaded() {
        if (lstPistes == null || lstPistes.size() != nbTracks) {
            return false;
        }
        return true;
    }

    public String getUrl() {

        // try {
        // playlist.createNewFile();
        // } catch (IOException e1) {
        // // TODO Auto-generated catch block
        // e1.printStackTrace();
        // }

        String adresse = "127.0.0.1";
        String port = "80";

        String url = null;

        url = "http://" + adresse + ":" + port + "/" + upnpId + ".m3u";

        return url;
    }

    public void setPistes(List<Piste> pistes) {
        lstPistes = pistes;

    }

    public List<Piste> getPistes() {
        return lstPistes;

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(titre);
        dest.writeInt(artisteId);
        dest.writeString(url);
        dest.writeString(albumArt);
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
        this.titre = in.readString();
        this.artisteId = in.readInt();
        this.url = in.readString();
        this.albumArt = in.readString();
        this.nbTracks = in.readInt();

    }

    @Override
    public int compareTo(Album arg0) {
        if (this.artiste.getNom().equals(arg0.artiste.getNom()))
            return this.titre.compareTo(arg0.titre);
        else {
            return this.artiste.getNom().compareTo(arg0.artiste.getNom());
        }

    }
}
