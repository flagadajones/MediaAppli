package fr.fladajonesjones.MediaControler.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils.InsertHelper;
import android.database.sqlite.SQLiteDatabase;
import fr.fladajonesjones.media.model.Radio;

public class RadioDAO {
    private SQLiteDatabase maBaseDonnees;
    private static RadioDAO instance;
    private static final Logger log = Logger.getLogger(RadioDAO.class.getName());
    public static RadioDAO getInstance() {
        if (instance == null) {
            instance = new RadioDAO();
        }
        return instance;
    }

    private RadioDAO() {
        maBaseDonnees = MySQLOpenHelper.instance.getBaseDonnees();
    }

    public ArrayList<Radio> getAllRadio() {
        Cursor c = maBaseDonnees.query(MySQLOpenHelper.TABLE_RADIOS, new String[] { MySQLOpenHelper.COLONNE_RADIO_ID,
                MySQLOpenHelper.COLONNE_RADIO_NOM, MySQLOpenHelper.COLONNE_RADIO_URL,
                MySQLOpenHelper.COLONNE_RADIO_ALBUM_ART, MySQLOpenHelper.COLONNE_RADIO_FAV }, null, null, null, null,
                MySQLOpenHelper.COLONNE_RADIO_NOM);

        ArrayList<Radio> retour = cursorToRadios(c);
        // Ferme le curseur pour liberer les ressources.
        c.close();
        return retour;
    }

    public ArrayList<Radio> getAllRadioFav(int number) {
        Cursor c = maBaseDonnees.query(MySQLOpenHelper.TABLE_RADIOS, new String[] { MySQLOpenHelper.COLONNE_RADIO_ID,
                MySQLOpenHelper.COLONNE_RADIO_NOM, MySQLOpenHelper.COLONNE_RADIO_URL,
                MySQLOpenHelper.COLONNE_RADIO_ALBUM_ART, MySQLOpenHelper.COLONNE_RADIO_FAV },
                MySQLOpenHelper.COLONNE_RADIO_FAV + " > 0 ", null, null, null, MySQLOpenHelper.COLONNE_RADIO_FAV
                        + " desc, " + MySQLOpenHelper.COLONNE_RADIO_NOM,  String.valueOf(number));

        ArrayList<Radio> retour = cursorToRadios(c);
        // Ferme le curseur pour liberer les ressources.
        c.close();
        return retour;
    }

    public ArrayList<Radio> getAllRadioFav() {
        Cursor c = maBaseDonnees.query(MySQLOpenHelper.TABLE_RADIOS, new String[] { MySQLOpenHelper.COLONNE_RADIO_ID,
                MySQLOpenHelper.COLONNE_RADIO_NOM, MySQLOpenHelper.COLONNE_RADIO_URL,
                MySQLOpenHelper.COLONNE_RADIO_ALBUM_ART, MySQLOpenHelper.COLONNE_RADIO_FAV },
                MySQLOpenHelper.COLONNE_RADIO_FAV + " > 0 ", null, null, null, MySQLOpenHelper.COLONNE_RADIO_FAV
                        + " desc, " + MySQLOpenHelper.COLONNE_RADIO_NOM, null);

        ArrayList<Radio> retour = cursorToRadios(c);
        // Ferme le curseur pour liberer les ressources.
        c.close();
        return retour;
    }

    private ArrayList<Radio> cursorToRadios(Cursor c) {
        // Si la requete ne renvoie pas de resultat.
        if (c.getCount() == 0)
            return new ArrayList<Radio>(0);
        ArrayList<Radio> retRadios = new ArrayList<Radio>(c.getCount());
        c.moveToFirst();
        do {
            Radio radio = new Radio(c.getString(MySQLOpenHelper.COLONNE_RADIO_ID_ID),
                    c.getString(MySQLOpenHelper.COLONNE_RADIO_NOM_ID),
                    c.getString(MySQLOpenHelper.COLONNE_RADIO_URL_ID),
                    c.getString(MySQLOpenHelper.COLONNE_RADIO_ALBUM_ART_ID),
                    c.getInt(MySQLOpenHelper.COLONNE_RADIO_FAV_ID));
            retRadios.add(radio);
        } while (c.moveToNext());
        return retRadios;
    }

    public Radio insertRadio(Radio radio) {
        ContentValues valeurs = new ContentValues();

        valeurs.put(MySQLOpenHelper.COLONNE_RADIO_ID, radio.upnpId);
        valeurs.put(MySQLOpenHelper.COLONNE_RADIO_NOM, radio.titre);
        valeurs.put(MySQLOpenHelper.COLONNE_RADIO_URL, radio.url);
        valeurs.put(MySQLOpenHelper.COLONNE_RADIO_ALBUM_ART, radio.albumArt);
        valeurs.put(MySQLOpenHelper.COLONNE_RADIO_FAV, radio.fav);

        maBaseDonnees.insert(MySQLOpenHelper.TABLE_RADIOS, null, valeurs);
        return radio;
    }

    public int updateRadio(Radio radio) {
        ContentValues valeurs = new ContentValues();

        valeurs.put(MySQLOpenHelper.COLONNE_RADIO_ID, radio.upnpId);
        valeurs.put(MySQLOpenHelper.COLONNE_RADIO_NOM, radio.titre);
        valeurs.put(MySQLOpenHelper.COLONNE_RADIO_URL, radio.url);
        valeurs.put(MySQLOpenHelper.COLONNE_RADIO_ALBUM_ART, radio.albumArt);
        valeurs.put(MySQLOpenHelper.COLONNE_RADIO_FAV, radio.fav);
        return maBaseDonnees.update(MySQLOpenHelper.TABLE_RADIOS, valeurs, MySQLOpenHelper.COLONNE_RADIO_ID + " = \""
                + radio.upnpId + "\"", null);
    }

    public void insertRadios(List<Radio> tmpRadios) {
        if (tmpRadios == null || tmpRadios.size() == 0)
            return;

        InsertHelper ih = new InsertHelper(maBaseDonnees, MySQLOpenHelper.TABLE_RADIOS);

        final int id = ih.getColumnIndex(MySQLOpenHelper.COLONNE_RADIO_ID);
        final int nom = ih.getColumnIndex(MySQLOpenHelper.COLONNE_RADIO_NOM);
        final int url = ih.getColumnIndex(MySQLOpenHelper.COLONNE_RADIO_URL);
        final int albumArt = ih.getColumnIndex(MySQLOpenHelper.COLONNE_RADIO_ALBUM_ART);
        final int fav = ih.getColumnIndex(MySQLOpenHelper.COLONNE_RADIO_FAV);

        HashMap<String, Integer> oldRadioFav = new HashMap<String, Integer>();

        List<Radio> oldRadioList = getAllRadioFav();
        if (!oldRadioList.isEmpty()) {
            for (Radio radio : oldRadioList) {
                oldRadioFav.put(radio.upnpId, radio.fav);
            }
            for (Radio radio : tmpRadios) {
                if (oldRadioFav.containsKey(radio.upnpId))
                    radio.fav = oldRadioFav.get(radio.upnpId);
            }
        }

     //   maBaseDonnees.setLockingEnabled(false);

        maBaseDonnees.execSQL("Delete from " + MySQLOpenHelper.TABLE_RADIOS);
        maBaseDonnees.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE name='" + MySQLOpenHelper.TABLE_RADIOS + "';");
        try {
            for (Radio radio : tmpRadios) {
                ih.prepareForReplace();
                ih.bind(id, radio.upnpId);
                ih.bind(nom, radio.titre);
                ih.bind(url, radio.url);
                ih.bind(albumArt, radio.albumArt);
                ih.bind(fav, radio.fav);
                ih.execute();
            }
        } catch (Exception e) {
            log.log(Level.SEVERE,"Error",e);
        } finally {
            if (ih != null)
                ih.close();
//            maBaseDonnees.setLockingEnabled(true);
        }
      //  tmpRadios.clear();
        // Application.activity.showToast("Pistes OK", true);
    }

}
