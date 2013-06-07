package fr.fladajonesjones.MediaControler.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils.InsertHelper;
import android.database.sqlite.SQLiteDatabase;
import fr.fladajonesjones.media.model.Album;
import fr.fladajonesjones.media.model.Artiste;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AlbumDAO {
    private SQLiteDatabase maBaseDonnees;
    private static final Logger log = Logger.getLogger(AlbumDAO.class.getName());

    public AlbumDAO() {
        maBaseDonnees = MySQLOpenHelper.instance.getBaseDonnees();
    }

    public Album getAlbum(String id) {

        Cursor c = maBaseDonnees.query(MySQLOpenHelper.TABLE_ALBUMS, new String[]{MySQLOpenHelper.COLONNE_ALBUM_ID,
                MySQLOpenHelper.COLONNE_ALBUM_NOM, MySQLOpenHelper.COLONNE_ALBUM_NB_TRACK,
                MySQLOpenHelper.COLONNE_ALBUM_ARTISTE_ID, MySQLOpenHelper.COLONNE_ALBUM_ALBUM_ART}, null, null, null,
                MySQLOpenHelper.COLONNE_ALBUM_ID + " = \"" + id + "\"", null);

        Album retour = cursorToAlbum(c);
        // Ferme le curseur pour liberer les ressources.
        c.close();

        return retour;
    }

    public Album getAlbumByName(String nom) {

        Cursor c = maBaseDonnees.query(MySQLOpenHelper.TABLE_ALBUMS, new String[]{MySQLOpenHelper.COLONNE_ALBUM_ID,
                MySQLOpenHelper.COLONNE_ALBUM_NOM, MySQLOpenHelper.COLONNE_ALBUM_NB_TRACK,
                MySQLOpenHelper.COLONNE_ALBUM_ARTISTE_ID, MySQLOpenHelper.COLONNE_ALBUM_ALBUM_ART}, null, null, null,
                MySQLOpenHelper.COLONNE_ALBUM_NOM + " LIKE \"" + nom + "\"", null);

        Album retour = cursorToAlbum(c);
        // Ferme le curseur pour liberer les ressources.
        c.close();

        return retour;
    }

    public ArrayList<Album> getAllAlbums() {

        Cursor c = maBaseDonnees.query(MySQLOpenHelper.TABLE_ALBUMS, new String[]{MySQLOpenHelper.COLONNE_ALBUM_ID,
                MySQLOpenHelper.COLONNE_ALBUM_NOM, MySQLOpenHelper.COLONNE_ALBUM_NB_TRACK,
                MySQLOpenHelper.COLONNE_ALBUM_ARTISTE_ID, MySQLOpenHelper.COLONNE_ALBUM_ALBUM_ART}, null, null, null,
                null, MySQLOpenHelper.COLONNE_ALBUM_NOM);

        ArrayList<Album> retour = cursorToAlbums(c, false);

        // Ferme le curseur pour liberer les ressources.
        c.close();

        return retour;
    }

    public ArrayList<Album> getAllAlbums(boolean complet, int number) {
        Long debut = System.currentTimeMillis();

        if (complet == false) {
            return getAllAlbums();
        }
        String SELECT_QUERY = "SELECT t1." + MySQLOpenHelper.COLONNE_ALBUM_ID + ",t1."
                + MySQLOpenHelper.COLONNE_ALBUM_NOM + "," + MySQLOpenHelper.COLONNE_ALBUM_NB_TRACK + ","
                + MySQLOpenHelper.COLONNE_ALBUM_ARTISTE_ID + "," + MySQLOpenHelper.COLONNE_ALBUM_ALBUM_ART + ",t2."
                + MySQLOpenHelper.COLONNE_ARTISTE_NOM + "," + MySQLOpenHelper.COLONNE_ARTISTE_NB_ALBUM

                + " FROM " + MySQLOpenHelper.TABLE_ALBUMS + " t1 INNER JOIN " + MySQLOpenHelper.TABLE_ARTISTES
                + " t2 ON t1." + MySQLOpenHelper.COLONNE_ALBUM_ARTISTE_ID + " = t2."
                + MySQLOpenHelper.COLONNE_ARTISTE_ID + " ORDER BY t1." + MySQLOpenHelper.COLONNE_ALBUM_ORDER;
        if (number != 0)
            SELECT_QUERY += " LIMIT " + number;
        Cursor c = maBaseDonnees.rawQuery(SELECT_QUERY, null);

        ArrayList<Album> retour = cursorToAlbums(c, true);

        // Ferme le curseur pour liberer les ressources.
        c.close();
        log.log(Level.WARNING, "getAllAlbums :" + number + " - " + (System.currentTimeMillis() - debut));
        return retour;
    }

    public ArrayList<Album> getAllAlbums(int artiste) {

        Cursor c = maBaseDonnees.query(MySQLOpenHelper.TABLE_ALBUMS, new String[]{MySQLOpenHelper.COLONNE_ALBUM_ID,
                MySQLOpenHelper.COLONNE_ALBUM_NOM, MySQLOpenHelper.COLONNE_ALBUM_NB_TRACK,
                MySQLOpenHelper.COLONNE_ALBUM_ARTISTE_ID, MySQLOpenHelper.COLONNE_ALBUM_ALBUM_ART},
                MySQLOpenHelper.COLONNE_ALBUM_ARTISTE_ID + " = " + artiste, null, null, null,
                MySQLOpenHelper.COLONNE_ALBUM_NOM);

        ArrayList<Album> retour = cursorToAlbums(c, false);
        // Ferme le curseur pour liberer les ressources.
        c.close();

        return retour;
    }

    private Album cursorToAlbum(Cursor c) {
        // Si la requete ne renvoie pas de resultat.
        if (c.getCount() == 0)
            return null;
        Album retAlbum = null;
        c.moveToFirst();
        retAlbum = new Album(c.getString(MySQLOpenHelper.COLONNE_ALBUM_ID_ID),
                c.getString(MySQLOpenHelper.COLONNE_ALBUM_NOM_ID), c.getInt(MySQLOpenHelper.COLONNE_ALBUM_NB_TRACK_ID),
                c.getInt(MySQLOpenHelper.COLONNE_ALBUM_ARTISTE_ID_ID),
                c.getString(MySQLOpenHelper.COLONNE_ALBUM_ALBUM_ART_ID));

        return retAlbum;
    }

    public ArrayList<Album> cursorToAlbums(Cursor c, boolean complet) {
        // Si la requete ne renvoie pas de resultat.
        int taille = c.getCount();
        if (taille == 0) {
            return new ArrayList<Album>(0);
        }
        ArrayList<Album> retArtites = new ArrayList<Album>(taille);
        c.moveToFirst();
        // log.warning("temps debut " + (System.currentTimeMillis()-debut));
        do {

            Album album = new Album(c.getString(MySQLOpenHelper.COLONNE_ALBUM_ID_ID),
                    c.getString(MySQLOpenHelper.COLONNE_ALBUM_NOM_ID),
                    c.getInt(MySQLOpenHelper.COLONNE_ALBUM_NB_TRACK_ID),
                    c.getInt(MySQLOpenHelper.COLONNE_ALBUM_ARTISTE_ID_ID),
                    c.getString(MySQLOpenHelper.COLONNE_ALBUM_ALBUM_ART_ID));
            // log.warning("temps album " + (System.currentTimeMillis()-debut));
            if (complet) {
                album.artiste = new Artiste(c.getInt(MySQLOpenHelper.COLONNE_ALBUM_ARTISTE_ID_ID), c.getString(5),
                        c.getInt(6));
            }
            // log.warning("temps artiste " + (System.currentTimeMillis()-debut));
            retArtites.add(album);
        } while (c.moveToNext());
        // Ferme le curseur pour lib�rer les ressources.
        return retArtites;
    }

    public Album insertAlbum(Album album) {

        ContentValues valeurs = new ContentValues();

        valeurs.put(MySQLOpenHelper.COLONNE_ALBUM_ID, album.upnpId);
        valeurs.put(MySQLOpenHelper.COLONNE_ALBUM_NOM, album.titre);
        valeurs.put(MySQLOpenHelper.COLONNE_ALBUM_NB_TRACK, album.nbTracks);
        valeurs.put(MySQLOpenHelper.COLONNE_ALBUM_ARTISTE_ID, album.artisteId);
        valeurs.put(MySQLOpenHelper.COLONNE_ALBUM_ALBUM_ART, album.albumArt);
        maBaseDonnees.insert(MySQLOpenHelper.TABLE_ALBUMS, null, valeurs);
        return album;
    }

    // public long insertAlbum(ContentValues valeurs) {
    // return maBaseDonnees.insert(MySQLOpenHelper.TABLE_ALBUMS, null, valeurs);
    // }

    public int updateAlbum(Album album) {

        ContentValues valeurs = new ContentValues();

        valeurs.put(MySQLOpenHelper.COLONNE_ALBUM_ID, album.upnpId);
        valeurs.put(MySQLOpenHelper.COLONNE_ALBUM_NOM, album.titre);
        valeurs.put(MySQLOpenHelper.COLONNE_ALBUM_NB_TRACK, album.nbTracks);
        valeurs.put(MySQLOpenHelper.COLONNE_ALBUM_ARTISTE_ID, album.artisteId);
        valeurs.put(MySQLOpenHelper.COLONNE_ALBUM_ALBUM_ART, album.albumArt);
        return maBaseDonnees.update(MySQLOpenHelper.TABLE_ALBUMS, valeurs, MySQLOpenHelper.COLONNE_ALBUM_ID + " = \""
                + album.upnpId + "\"", null);
    }

    public int updateAlbum(ContentValues valeurs, String where, String[] whereArgs) {

        return maBaseDonnees.update(MySQLOpenHelper.TABLE_ALBUMS, valeurs, where, whereArgs);
    }

    public int removeAlbum(int id) {

        return maBaseDonnees.delete(MySQLOpenHelper.TABLE_ALBUMS, MySQLOpenHelper.COLONNE_ALBUM_ID + " = " + id, null);
    }

    public int removeAlbum(String where, String[] whereArgs) {

        return maBaseDonnees.delete(MySQLOpenHelper.TABLE_ALBUMS, where, whereArgs);
    }

    public void insertAlbums(List<Album> tmpAlbums) {
        if (tmpAlbums == null || tmpAlbums.size() == 0)
            return;
        // Application.activity.showToast("Insert Albums", true);
        // The InsertHelper needs to have the db instance + the name of the
        // table where you want to add the data
        InsertHelper ih = new InsertHelper(maBaseDonnees, MySQLOpenHelper.TABLE_ALBUMS);

        final int albumArt = ih.getColumnIndex(MySQLOpenHelper.COLONNE_ALBUM_ALBUM_ART);
        final int artisteId = ih.getColumnIndex(MySQLOpenHelper.COLONNE_ALBUM_ARTISTE_ID);
        final int albumId = ih.getColumnIndex(MySQLOpenHelper.COLONNE_ALBUM_ID);
        final int nbTrack = ih.getColumnIndex(MySQLOpenHelper.COLONNE_ALBUM_NB_TRACK);
        final int albumNom = ih.getColumnIndex(MySQLOpenHelper.COLONNE_ALBUM_NOM);
        final int ordre = ih.getColumnIndex(MySQLOpenHelper.COLONNE_ALBUM_ORDER);

        Collections.sort(tmpAlbums);

        // maBaseDonnees.setLockingEnabled(false);
        int number = 1;
        try {
            for (Album album : tmpAlbums) {
                ih.prepareForReplace();
                ih.bind(albumArt, album.albumArt);
                ih.bind(artisteId, album.artisteId);
                ih.bind(albumId, album.upnpId);
                ih.bind(nbTrack, album.nbTracks);
                ih.bind(albumNom, album.titre);
                ih.bind(ordre, number);

                ih.execute();
                number++;
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "erreur", e);
        } finally {
            if (ih != null)
                ih.close();
            //        maBaseDonnees.setLockingEnabled(true);
        }
        tmpAlbums.clear();

        // Application.activity.showToast("Albums OK", true);
        // Application.instance.unregisterReceiver(mStatusListener);


    }

}
