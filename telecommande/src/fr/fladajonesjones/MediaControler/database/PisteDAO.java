package fr.fladajonesjones.MediaControler.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import fr.fladajonesjones.MediaControler.model.Piste;

public class PisteDAO {
    private SQLiteDatabase maBaseDonnees;

    public PisteDAO() {
        maBaseDonnees = MySQLOpenHelper.instance.getBaseDonnees();
    }

    public Piste getPiste(int id) {
        Cursor c = maBaseDonnees.query(MySQLOpenHelper.TABLE_PISTES, new String[] { MySQLOpenHelper.COLONNE_PISTE_ID,
                MySQLOpenHelper.COLONNE_PISTE_NOM, MySQLOpenHelper.COLONNE_PISTE_DUREE,
                MySQLOpenHelper.COLONNE_PISTE_ALBUM_ID, MySQLOpenHelper.COLONNE_PISTE_URL },
                MySQLOpenHelper.COLONNE_PISTE_ID + " = " + id, null, null, null, null);

        Piste retour = cursorToPiste(c);
        // Ferme le curseur pour liberer les ressources.
        c.close();
        return retour;
    }

    public Piste getPiste(String nom) {
        Cursor c = maBaseDonnees.query(MySQLOpenHelper.TABLE_PISTES, new String[] { MySQLOpenHelper.COLONNE_PISTE_ID,
                MySQLOpenHelper.COLONNE_PISTE_NOM, MySQLOpenHelper.COLONNE_PISTE_DUREE,
                MySQLOpenHelper.COLONNE_PISTE_ALBUM_ID, MySQLOpenHelper.COLONNE_PISTE_URL },
                MySQLOpenHelper.COLONNE_PISTE_NOM + " LIKE \"" + nom + "\"", null, null, null, null);

        Piste retour = cursorToPiste(c);
        // Ferme le curseur pour liberer les ressources.
        c.close();
        return retour;
    }

    public ArrayList<Piste> getAllPistes() {
        Cursor c = maBaseDonnees.query(MySQLOpenHelper.TABLE_PISTES, new String[] { MySQLOpenHelper.COLONNE_PISTE_ID,
                MySQLOpenHelper.COLONNE_PISTE_NOM, MySQLOpenHelper.COLONNE_PISTE_DUREE,
                MySQLOpenHelper.COLONNE_PISTE_ALBUM_ID, MySQLOpenHelper.COLONNE_PISTE_URL }, null, null, null, null,
                MySQLOpenHelper.COLONNE_PISTE_NOM);

        ArrayList<Piste> retour = cursorToPistes(c);
        // Ferme le curseur pour liberer les ressources.
        c.close();
        return retour;
    }

    public ArrayList<Piste> getAllPistes(String album) {
        Cursor c = maBaseDonnees.query(MySQLOpenHelper.TABLE_PISTES, new String[] { MySQLOpenHelper.COLONNE_PISTE_ID,
                MySQLOpenHelper.COLONNE_PISTE_NOM, MySQLOpenHelper.COLONNE_PISTE_DUREE,
                MySQLOpenHelper.COLONNE_PISTE_ALBUM_ID, MySQLOpenHelper.COLONNE_PISTE_URL },
                MySQLOpenHelper.COLONNE_PISTE_ALBUM_ID + " = \"" + album + "\"", null, null, null, null);

        ArrayList<Piste> retour = cursorToPistes(c);
        // Ferme le curseur pour libErer les ressources.
        c.close();
        return retour;
    }

    private Piste cursorToPiste(Cursor c) {
        // Si la requete ne renvoie pas de resultat.
        if (c.getCount() == 0)
            return null;
        c.moveToFirst();
        Piste retPiste = new Piste(c.getString(MySQLOpenHelper.COLONNE_PISTE_ID_ID),
                c.getString(MySQLOpenHelper.COLONNE_PISTE_NOM_ID), c.getString(MySQLOpenHelper.COLONNE_PISTE_DUREE_ID),
                c.getString(MySQLOpenHelper.COLONNE_PISTE_ALBUM_ID_ID),
                c.getString(MySQLOpenHelper.COLONNE_PISTE_URL_ID));

        return retPiste;
    }

    private ArrayList<Piste> cursorToPistes(Cursor c) {
        // Si la requete ne renvoie pas de resultat.
        if (c.getCount() == 0)
            return new ArrayList<Piste>(0);
        ArrayList<Piste> retArtites = new ArrayList<Piste>(c.getCount());
        c.moveToFirst();
        do {
            Piste piste = new Piste(c.getString(MySQLOpenHelper.COLONNE_PISTE_ID_ID),
                    c.getString(MySQLOpenHelper.COLONNE_PISTE_NOM_ID),
                    c.getString(MySQLOpenHelper.COLONNE_PISTE_DUREE_ID),
                    c.getString(MySQLOpenHelper.COLONNE_PISTE_ALBUM_ID_ID),
                    c.getString(MySQLOpenHelper.COLONNE_PISTE_URL_ID));
            retArtites.add(piste);
        } while (c.moveToNext());
        return retArtites;
    }

    public Piste insertPiste(Piste piste) {
        ContentValues valeurs = new ContentValues();

        valeurs.put(MySQLOpenHelper.COLONNE_PISTE_ID, piste.upnpId);
        valeurs.put(MySQLOpenHelper.COLONNE_PISTE_NOM, piste.nom);
        valeurs.put(MySQLOpenHelper.COLONNE_PISTE_DUREE, piste.duree);
        valeurs.put(MySQLOpenHelper.COLONNE_PISTE_ALBUM_ID, piste.albumId);
        valeurs.put(MySQLOpenHelper.COLONNE_PISTE_URL, piste.url);
        maBaseDonnees.insert(MySQLOpenHelper.TABLE_PISTES, null, valeurs);
        return piste;
    }

    public int updatePiste(Piste piste) {
        ContentValues valeurs = new ContentValues();

        valeurs.put(MySQLOpenHelper.COLONNE_PISTE_ID, piste.upnpId);
        valeurs.put(MySQLOpenHelper.COLONNE_PISTE_NOM, piste.nom);
        valeurs.put(MySQLOpenHelper.COLONNE_PISTE_DUREE, piste.duree);
        valeurs.put(MySQLOpenHelper.COLONNE_PISTE_ALBUM_ID, piste.albumId);
        valeurs.put(MySQLOpenHelper.COLONNE_PISTE_URL, piste.url);
        return maBaseDonnees.update(MySQLOpenHelper.TABLE_PISTES, valeurs, MySQLOpenHelper.COLONNE_PISTE_ID + " = \""
                + piste.upnpId + "\"", null);
    }

    public int updatePiste(ContentValues valeurs, String where, String[] whereArgs) {
        return maBaseDonnees.update(MySQLOpenHelper.TABLE_PISTES, valeurs, where, whereArgs);
    }

    public int removePiste(String nom) {
        return maBaseDonnees.delete(MySQLOpenHelper.TABLE_PISTES, MySQLOpenHelper.COLONNE_PISTE_NOM + " LIKE \"" + nom
                + "\"", null);
    }

    public int removePiste(int id) {
        return maBaseDonnees.delete(MySQLOpenHelper.TABLE_PISTES, MySQLOpenHelper.COLONNE_PISTE_ID + " = " + id, null);
    }

    public int removePiste(String where, String[] whereArgs) {
        return maBaseDonnees.delete(MySQLOpenHelper.TABLE_PISTES, where, whereArgs);
    }

}
