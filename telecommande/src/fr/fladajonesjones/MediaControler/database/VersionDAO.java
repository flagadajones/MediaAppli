package fr.fladajonesjones.MediaControler.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class VersionDAO {
    private SQLiteDatabase maBaseDonnees;

    public VersionDAO() {
        maBaseDonnees = MySQLOpenHelper.instance.getBaseDonnees();
    }

    public String getRadioVersion() {
        Cursor c = maBaseDonnees.query(MySQLOpenHelper.TABLE_VERSIONS,
                new String[]{MySQLOpenHelper.COLONNE_VERSION_VALUE}, MySQLOpenHelper.COLONNE_VERSION_ID + " = '"
                + MySQLOpenHelper.VERSION_RADIO + "'", null, null, null, null);

        if (c.getCount() == 0)
            return null;
        c.moveToFirst();
        String version = c.getString(0);

        c.close();
        return version;
    }

    public String getMusiqueVersion() {
        Cursor c = maBaseDonnees.query(MySQLOpenHelper.TABLE_VERSIONS,
                new String[]{MySQLOpenHelper.COLONNE_VERSION_VALUE}, MySQLOpenHelper.COLONNE_VERSION_ID + " = '"
                + MySQLOpenHelper.VERSION_MUSIQUE + "'", null, null, null, null);

        if (c.getCount() == 0)
            return null;
        c.moveToFirst();
        String version = c.getString(0);

        c.close();
        return version;
    }

    public int updateVersionRadio(String version) {
        ContentValues valeurs = new ContentValues();

        valeurs.put(MySQLOpenHelper.COLONNE_VERSION_VALUE, version);
        return maBaseDonnees.update(MySQLOpenHelper.TABLE_VERSIONS, valeurs, MySQLOpenHelper.COLONNE_VERSION_ID
                + " = \"" + MySQLOpenHelper.VERSION_RADIO + "\"", null);
    }

    public int updateVersionMusique(String version) {
        ContentValues valeurs = new ContentValues();

        valeurs.put(MySQLOpenHelper.COLONNE_VERSION_VALUE, version);
        return maBaseDonnees.update(MySQLOpenHelper.TABLE_VERSIONS, valeurs, MySQLOpenHelper.COLONNE_VERSION_ID
                + " = \"" + MySQLOpenHelper.VERSION_MUSIQUE + "\"", null);
    }

}
