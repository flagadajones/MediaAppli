package fr.fladajonesjones.MediaControler.model;

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
    
    
    public String getUrl(){
        return url;
    }
    public String getMetaData() {
        if (metaData == null) {
            DIDLContent didl = new DIDLContent();

            AudioBroadcast item = new AudioBroadcast();
            // item.setRadioBand(radio.nom);
            item.setTitle(nom);
            item.setId(nom);
            item.setParentID("0");
            // item.setRadioCallSign(radio.nom);
            item.addResource(new Res(MimeType.valueOf("audio/mp3"), null, url));

            didl.addItem(item);

            try {
                item.addProperty(new DIDLObject.Property.UPNP.ALBUM_ART_URI(new URI(icone)));
                metaData = new DIDLParser().generate(didl);
            } catch (Exception e1) {
                // Toast.makeText(Application.instance, e1.getMessage(), Toast.LENGTH_LONG).show();
                metaData = "NO METADATA";

            }
        }

        return metaData;
    }

    public Radio(String nom, String icone, String url) {
        this.nom = nom;
        this.icone = icone;
        this.url = url;

    }

    public Radio() {

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
        dest.writeString(this.nom);
        dest.writeString(this.icone);
        dest.writeString(this.url);
    }

    // On va ici hydrater notre objet a partir du Parcel
    public void getFromParcel(Parcel in) {
        nom = in.readString();
        icone = in.readString();
        url = in.readString();

    }

    @Override
    public long getDuration() {
        // TODO Auto-generated method stub
        return 0;
    }

}
