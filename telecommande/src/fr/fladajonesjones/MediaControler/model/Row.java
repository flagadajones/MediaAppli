package fr.fladajonesjones.MediaControler.model;

import fr.fladajonesjones.media.model.Album;
import fr.fladajonesjones.media.model.Artiste;

import java.util.ArrayList;
import java.util.List;

public class Row {
    public List<RowArtiste> lstArtiste = new ArrayList<RowArtiste>(5);
    public List<Album> lstAlbum = new ArrayList<Album>(5);

    public String toString() {
        String retour = "TOTO\n";
        for (RowArtiste iterable_element : lstArtiste) {
            retour += iterable_element.toString() + " == ";
        }
        retour += "\n    ";
        for (Album iterable_element : lstAlbum) {
            retour += "AL " + iterable_element.upnpId + " - " + iterable_element.artisteId + "   == ";
        }
        return retour;
    }

    public static class RowArtiste {
        public Artiste artiste;
        public int nbAlbum = 0;
        public int color;

        public String toString() {
            String retour = "";
            retour = "Ar " + artiste.getId() + " : " + nbAlbum;

            return retour;
        }


    }
}
