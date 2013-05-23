package fr.fladajonesjones.MediaControler.database;

import java.util.logging.Logger;

import android.content.Context;
import android.content.res.Resources;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLOpenHelper extends SQLiteOpenHelper {

    SQLiteDatabase maBaseDonnees;
    Context mCtx;
    Resources res;

    public static MySQLOpenHelper instance;

    private MySQLOpenHelper(Context context) {
        super(context, BASE_NOM, null, BASE_VERSION);
        mCtx = context;
        res = context.getResources();
        instance=this;
    }

    private MySQLOpenHelper open() throws SQLException {
        maBaseDonnees = instance.getWritableDatabase();

        return this;

    }

    public static void closeDB() {
        instance.close();

    }

    public static final MySQLOpenHelper getInstance(Context c) {
if(instance==null){
	instance=new MySQLOpenHelper(c);
	instance.open();
}
return instance;


    }

    private static final Logger log = Logger.getLogger(MySQLOpenHelper.class.getName());

    public SQLiteDatabase getBaseDonnees() {
        return maBaseDonnees;
    }

    public static final int BASE_VERSION = 2;
    public static final String BASE_NOM = "flagadajones.mediarenderer.db";

    public static final String TABLE_ARTISTES = "artistes";
    public static final String TABLE_ARTISTES_INDEX = "artiste_nom";
    public static final String COLONNE_ARTISTE_ID = "id";
    public static final String COLONNE_ARTISTE_NOM = "nom";
    public static final String COLONNE_ARTISTE_NB_ALBUM = "nbAlbums";

    public static final int COLONNE_ARTISTE_ID_ID = 0;
    public static final int COLONNE_ARTISTE_NOM_ID = 1;
    public static final int COLONNE_ARTISTE_NB_ALBUM_ID = 2;

    public static final String TABLE_ALBUMS = "albums";
    public static final String TABLE_ALBUMS_INDEX = "album_nom";
    public static final String COLONNE_ALBUM_ID = "id";
    // public static final String COLONNE_ALBUM_UPNP_ID = "upnpId";
    public static final String COLONNE_ALBUM_NOM = "nom";
    public static final String COLONNE_ALBUM_NB_TRACK = "nbTracks";
    public static final String COLONNE_ALBUM_ARTISTE_ID = "artisteId";
    public static final String COLONNE_ALBUM_ALBUM_ART = "albumArt";
    public static final String COLONNE_ALBUM_ORDER = "ordre";

    public static final int COLONNE_ALBUM_ID_ID = 0;
    // public static final int COLONNE_ALBUM_UPNP_ID_ID = 1;
    public static final int COLONNE_ALBUM_NOM_ID = 1;
    public static final int COLONNE_ALBUM_NB_TRACK_ID = 2;
    public static final int COLONNE_ALBUM_ARTISTE_ID_ID = 3;
    public static final int COLONNE_ALBUM_ALBUM_ART_ID = 4;

    public static final String TABLE_PISTES = "pistes";
    public static final String TABLE_PISTES_INDEX = "pistes_album";
    public static final String COLONNE_PISTE_ID = "Id";
    // public static final String COLONNE_PISTE_UPNP_ID = "upnpId";
    public static final String COLONNE_PISTE_NOM = "nom";
    public static final String COLONNE_PISTE_DUREE = "duree";
    public static final String COLONNE_PISTE_ALBUM_ID = "albumId";
    public static final String COLONNE_PISTE_URL = "url";

    public static final int COLONNE_PISTE_ID_ID = 0;
    // public static final int COLONNE_PISTE_UPNP_ID_ID = 1;
    public static final int COLONNE_PISTE_NOM_ID = 1;
    public static final int COLONNE_PISTE_DUREE_ID = 2;
    public static final int COLONNE_PISTE_ALBUM_ID_ID = 3;
    public static final int COLONNE_PISTE_URL_ID = 4;

    public static final String TABLE_DEVICES = "devices";
    public static final String TABLE_DEVICES_INDEX = "devices_udn";
    public static final String COLONNE_DEVICE_ID = "Id";
    public static final String COLONNE_DEVICE_UDN = "udn";
    public static final String COLONNE_DEVICE_TYPE = "type";
    // public static final String COLONNE_DEVICE_DEFAULT = "default";

    private static final String REQUETE_CREATION_TABLE_DEVICES = "create table " + TABLE_DEVICES + " ("
            + COLONNE_DEVICE_ID + " integer primary key autoincrement, " + COLONNE_DEVICE_UDN + " text not null, "
            + COLONNE_DEVICE_TYPE + " integer not null);";
    private static final String REQUETE_CREATION_TABLE_DEVICES_INDEX = "create index " + TABLE_DEVICES_INDEX + " on "
            + TABLE_DEVICES + "(" + COLONNE_DEVICE_UDN + ")";

    private static final String REQUETE_CREATION_TABLE_ARTITES = "create table " + TABLE_ARTISTES + " ("
            + COLONNE_ARTISTE_ID + " integer primary key autoincrement, " + COLONNE_ARTISTE_NOM + " text not null, "
            + COLONNE_ARTISTE_NB_ALBUM + " integer not null);";
    private static final String REQUETE_CREATION_TABLE_ARTISTES_INDEX = "create index " + TABLE_ARTISTES_INDEX + " on "
            + TABLE_ARTISTES + "(" + COLONNE_ARTISTE_NOM + ")";


    private static final String REQUETE_CREATION_TABLE_ALBUMS = "create table " + TABLE_ALBUMS + " ("
            + COLONNE_ALBUM_ID + " text primary key , " + COLONNE_ALBUM_NOM + " text not null, "
            + COLONNE_ALBUM_ALBUM_ART + " text not null, " + COLONNE_ALBUM_ARTISTE_ID + " integer not null, "
            + COLONNE_ALBUM_NB_TRACK + " integer not null,"
            + COLONNE_ALBUM_ORDER + " integer not null,"
            + "FOREIGN KEY(" + COLONNE_ALBUM_ARTISTE_ID
            + ") REFERENCES " + TABLE_ARTISTES + "(" + COLONNE_ARTISTE_ID + ")" + ");";
    private static final String REQUETE_CREATION_TABLE_ALBUMS_INDEX = "create index " + TABLE_ALBUMS_INDEX + " on "
            + TABLE_ALBUMS + "(" + COLONNE_ALBUM_ORDER + ")";

    private static final String REQUETE_CREATION_TABLE_PISTES = "create table " + TABLE_PISTES + " ("
            + COLONNE_PISTE_ID + " string primary key , " + COLONNE_PISTE_NOM + " text not null, "
            + COLONNE_PISTE_DUREE + " text not null, " + COLONNE_PISTE_ALBUM_ID + " integer not null, "
            + COLONNE_PISTE_URL + " text not null," + "FOREIGN KEY(" + COLONNE_PISTE_ALBUM_ID + ") REFERENCES "
            + TABLE_ALBUMS + "(" + COLONNE_ALBUM_ID + ")" + ");";
    private static final String REQUETE_CREATION_TABLE_PISTES_INDEX = "create index " + TABLE_PISTES_INDEX + " on "
            + TABLE_PISTES + "(" + COLONNE_PISTE_ALBUM_ID + ")";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(REQUETE_CREATION_TABLE_DEVICES);
        db.execSQL(REQUETE_CREATION_TABLE_ARTITES);
        db.execSQL(REQUETE_CREATION_TABLE_ALBUMS);
        db.execSQL(REQUETE_CREATION_TABLE_PISTES);

        db.execSQL(REQUETE_CREATION_TABLE_ALBUMS_INDEX);
        db.execSQL(REQUETE_CREATION_TABLE_DEVICES_INDEX);
        db.execSQL(REQUETE_CREATION_TABLE_ARTISTES_INDEX);
        db.execSQL(REQUETE_CREATION_TABLE_PISTES_INDEX);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP INDEX " + TABLE_ALBUMS_INDEX + ";");
        db.execSQL("DROP INDEX " + TABLE_DEVICES_INDEX + ";");
        db.execSQL("DROP INDEX " + TABLE_PISTES_INDEX + ";");
        db.execSQL("DROP INDEX " + TABLE_ARTISTES_INDEX + ";");

       db.execSQL("DROP TABLE " + TABLE_DEVICES + ";");
        db.execSQL("DROP TABLE " + TABLE_PISTES + ";");
        db.execSQL("DROP TABLE " + TABLE_ALBUMS + ";");
        db.execSQL("DROP TABLE " + TABLE_ARTISTES + ";");

     // Creation de la nouvelle structure.
        onCreate(db);
    }
    
    
//    private void insertAlbums(SQLiteDatabase db, List<Album> tmpAlbums) {
//        if (tmpAlbums == null || tmpAlbums.size() == 0)
//            return;
//        InsertHelper ih = new InsertHelper(db, MySQLOpenHelper.TABLE_ALBUMS);
//
//        final int albumArt = ih.getColumnIndex(MySQLOpenHelper.COLONNE_ALBUM_ALBUM_ART);
//        final int artisteId = ih.getColumnIndex(MySQLOpenHelper.COLONNE_ALBUM_ARTISTE_ID);
//        final int albumId = ih.getColumnIndex(MySQLOpenHelper.COLONNE_ALBUM_ID);
//        final int nbTrack = ih.getColumnIndex(MySQLOpenHelper.COLONNE_ALBUM_NB_TRACK);
//        final int albumNom = ih.getColumnIndex(MySQLOpenHelper.COLONNE_ALBUM_NOM);
//        final int order = ih.getColumnIndex(MySQLOpenHelper.COLONNE_ALBUM_ORDER);
//
//        db.setLockingEnabled(false);
//        try {
//            for (Album album : tmpAlbums) {
//                ih.prepareForReplace();
//                ih.bind(albumArt, album.icone);
//                ih.bind(artisteId, album.artisteId);
//                ih.bind(albumId, album.upnpId);
//                ih.bind(nbTrack, album.nbTracks);
//                ih.bind(albumNom, album.nom);
//                ih.bind(order,album.order);
//                ih.execute();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (ih != null)
//                ih.close();
//            db.setLockingEnabled(true);
//        }
//        tmpAlbums.clear();
//
//    }

    
    public void initMock(){
        
        SQLiteDatabase db = instance.getWritableDatabase();
        db.execSQL("INSERT INTO " + TABLE_ARTISTES + "values('artiste 1',1);");
        db.execSQL("INSERT INTO " + TABLE_ARTISTES + "values('artiste 2',2);");
        db.execSQL("INSERT INTO " + TABLE_ARTISTES + "values('artiste 3',1);");
        db.execSQL("INSERT INTO " + TABLE_ARTISTES + "values('artiste 4',1);");
        
        db.execSQL("INSERT INTO " + TABLE_ALBUMS + "values('album1','html',1,13,1);");
        db.execSQL("INSERT INTO " + TABLE_ALBUMS + "values('album2','html',2,13,1);");
        db.execSQL("INSERT INTO " + TABLE_ALBUMS + "values('album3','html',2,13,1);");
        db.execSQL("INSERT INTO " + TABLE_ALBUMS + "values('album4','html',2,13,1);");
        db.execSQL("INSERT INTO " + TABLE_ALBUMS + "values('album5','html',3,13,1);");
        db.execSQL("INSERT INTO " + TABLE_ALBUMS + "values('album6','html',4,13,1);");
        db.execSQL("INSERT INTO " + TABLE_ALBUMS + "values('album7','html',4,13,1);");
        
        
    }
}
