package fr.fladajonesjones.MediaControler.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import fr.fladajonesjones.media.model.Artiste;

import java.util.ArrayList;

public class ArtisteDAO {
    private SQLiteDatabase maBaseDonnees;

    public ArtisteDAO() {
        maBaseDonnees = MySQLOpenHelper.instance.getBaseDonnees();
    }


    public Artiste getArtiste(int id) {
        Cursor c = maBaseDonnees.query(MySQLOpenHelper.TABLE_ARTISTES, new String[]{
                MySQLOpenHelper.COLONNE_ARTISTE_ID, MySQLOpenHelper.COLONNE_ARTISTE_NOM,
                MySQLOpenHelper.COLONNE_ARTISTE_NB_ALBUM}, MySQLOpenHelper.COLONNE_ARTISTE_ID
                + " = " + id, null, null, null, null);

        Artiste retour = cursorToArtiste(c);
        // Ferme le curseur pour liberer les ressources.
        c.close();
        return retour;

    }

    public Artiste getArtiste(String nom) {
        Cursor c = maBaseDonnees.query(MySQLOpenHelper.TABLE_ARTISTES, new String[]{
                MySQLOpenHelper.COLONNE_ARTISTE_ID, MySQLOpenHelper.COLONNE_ARTISTE_NOM,
                MySQLOpenHelper.COLONNE_ARTISTE_NB_ALBUM}, MySQLOpenHelper.COLONNE_ARTISTE_NOM
                + " LIKE \"" + nom + "\"", null, null, null, null);

        Artiste retour = cursorToArtiste(c);
        // Ferme le curseur pour liberer les ressources.
        c.close();
        return retour;
    }

    public ArrayList<Artiste> getAllArtistes(boolean complet) {

        Cursor c = maBaseDonnees
                .query(MySQLOpenHelper.TABLE_ARTISTES, new String[]{MySQLOpenHelper.COLONNE_ARTISTE_ID,
                        MySQLOpenHelper.COLONNE_ARTISTE_NOM, MySQLOpenHelper.COLONNE_ARTISTE_NB_ALBUM}, null, null,
                        null, null, MySQLOpenHelper.COLONNE_ARTISTE_NOM);
        ArrayList<Artiste> retour = cursorToArtistes(c, complet);
        // Ferme le curseur pour liberer les ressources.
        c.close();
        return retour;

    }

    private Artiste cursorToArtiste(Cursor c) {
        // Si la requete ne renvoie pas de resultat.
        if (c.getCount() == 0)
            return null;
        c.moveToFirst();
        Artiste retArtiste = new Artiste(c.getInt(MySQLOpenHelper.COLONNE_ARTISTE_ID_ID),
                c.getString(MySQLOpenHelper.COLONNE_ARTISTE_NOM_ID),
                c.getInt(MySQLOpenHelper.COLONNE_ARTISTE_NB_ALBUM_ID));

        return retArtiste;
    }

    private ArrayList<Artiste> cursorToArtistes(Cursor c, boolean complet) {
        // Si la requete ne renvoie pas de resultat.
        if (c.getCount() == 0)
            return new ArrayList<Artiste>(0);
        ArrayList<Artiste> retArtites = new ArrayList<Artiste>(c.getCount());
        c.moveToFirst();
        do {
            Artiste artiste = new Artiste(c.getInt(MySQLOpenHelper.COLONNE_ARTISTE_ID_ID),
                    c.getString(MySQLOpenHelper.COLONNE_ARTISTE_NOM_ID),
                    c.getInt(MySQLOpenHelper.COLONNE_ARTISTE_NB_ALBUM_ID));
            if (complet) {
                AlbumDAO albumDAO = new AlbumDAO();
                artiste.setLstAlbum(albumDAO.getAllAlbums(artiste.getId()));
            }

            retArtites.add(artiste);
        } while (c.moveToNext());
        return retArtites;
    }

    public Artiste insertArtiste(Artiste artiste) {
        artiste.setId(insertArtisteId(artiste));
        return artiste;

    }

    public int insertArtisteId(Artiste artiste) {

        ContentValues valeurs = new ContentValues();
        valeurs.put(MySQLOpenHelper.COLONNE_ARTISTE_NOM, artiste.getNom());
        valeurs.put(MySQLOpenHelper.COLONNE_ARTISTE_NB_ALBUM, artiste.getNbAlbum());
        int insert = (int) maBaseDonnees.insert(MySQLOpenHelper.TABLE_ARTISTES, null, valeurs);

        return insert;

    }


    public int updateArtiste(Artiste artisteToUpdate) {

        ContentValues valeurs = new ContentValues();
        valeurs.put(MySQLOpenHelper.COLONNE_ARTISTE_NOM, artisteToUpdate.getNom());
        valeurs.put(MySQLOpenHelper.COLONNE_ARTISTE_NB_ALBUM, artisteToUpdate.getNbAlbum());
        return maBaseDonnees.update(MySQLOpenHelper.TABLE_ARTISTES, valeurs, MySQLOpenHelper.COLONNE_ARTISTE_ID + " = "
                + artisteToUpdate.getId(), null);
    }

    public int updateArtiste(ContentValues valeurs, String where, String[] whereArgs) {

        return maBaseDonnees.update(MySQLOpenHelper.TABLE_ARTISTES, valeurs, where, whereArgs);
    }

    public int removeArtiste(String nom) {

        return maBaseDonnees.delete(MySQLOpenHelper.TABLE_ARTISTES, MySQLOpenHelper.COLONNE_ARTISTE_NOM + " LIKE \""
                + nom + "\"", null);
    }

    public int removeArtiste(int id) {

        return maBaseDonnees.delete(MySQLOpenHelper.TABLE_ARTISTES, MySQLOpenHelper.COLONNE_ARTISTE_ID + " = " + id,
                null);
    }

    public int removeArtiste(String where, String[] whereArgs) {

        return maBaseDonnees.delete(MySQLOpenHelper.TABLE_ARTISTES, where, whereArgs);
    }

}
