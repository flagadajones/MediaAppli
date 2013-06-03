package fr.fladajonesjones.media.model;

import java.net.URI;

import org.fourthline.cling.support.contentdirectory.DIDLParser;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.item.AudioBroadcast;
import org.seamless.util.MimeType;

import android.os.Parcel;
import android.os.Parcelable;

public class Radio extends Musique implements Parcelable {

    public Radio(String id,String nom,String url, String albumArt,int fav){
        this.upnpId=id;
        this.titre=nom;
        this.url=url;
        this.albumArt=albumArt;
        this.fav=fav;
    }
    public String getUrl() {
        return url;
    }

    public String getMetaData() {
        if (metaData == null) {
            DIDLContent didl = new DIDLContent();

            AudioBroadcast item = new AudioBroadcast();
            // item.setRadioBand(radio.nom);
            item.setTitle(titre);
            item.setId(titre);
            item.setParentID("0");
            // item.setRadioCallSign(radio.nom);
            item.addResource(new Res(MimeType.valueOf("audio/mp3"), null, url));

            didl.addItem(item);

            try {
                item.addProperty(new DIDLObject.Property.UPNP.ALBUM_ART_URI(new URI(albumArt)));
                metaData = new DIDLParser().generate(didl);
            } catch (Exception e1) {
                // Toast.makeText(Application.instance, e1.getMessage(), Toast.LENGTH_LONG).show();
                metaData = "NO METADATA";

            }
        }

        return metaData;
    }

    public Radio(String nom, String icone, String url) {
        this.titre = nom;
        this.albumArt = icone;
        this.url = url;

    }

    public Radio() {

    }
@Override
    public String getDuree() {
        return "00:00:00";
    }

    public Radio(Parcel in) {
        this.getFromParcel(in);
    }

    @SuppressWarnings("rawtypes")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Radio createFromParcel(Parcel in) {
            return new Radio(in);
        }

        @Override
        public Object[] newArray(int size) {
            return null;
        }
    };

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub
        // On ecrit dans le parcel les donnees de notre objet
        dest.writeString(this.titre);
        dest.writeString(this.albumArt);
        dest.writeString(this.url);
    }

    // On va ici hydrater notre objet a partir du Parcel
    public void getFromParcel(Parcel in) {
        titre = in.readString();
        albumArt = in.readString();
        url = in.readString();

    }

}
