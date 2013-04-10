package fr.fladajonesjones.MediaControler.upnp;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.support.contentdirectory.callback.Browse;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.container.MusicAlbum;
import org.fourthline.cling.support.model.item.Item;
import org.fourthline.cling.support.model.item.MusicTrack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DatabaseUtils.InsertHelper;
import android.database.sqlite.SQLiteDatabase;
import fr.fladajonesjones.MediaControler.Application;
import fr.fladajonesjones.MediaControler.database.AlbumDAO;
import fr.fladajonesjones.MediaControler.database.ArtisteDAO;
import fr.fladajonesjones.MediaControler.database.MySQLOpenHelper;
import fr.fladajonesjones.MediaControler.database.PisteDAO;
import fr.fladajonesjones.MediaControler.manager.UpnpDeviceManager;
import fr.fladajonesjones.MediaControler.model.Album;
import fr.fladajonesjones.MediaControler.model.Artiste;
import fr.fladajonesjones.MediaControler.model.Piste;

public class UpnpServerDevice extends UpnpDevice {
    private static final Logger log = Logger.getLogger(UpnpServerDevice.class
            .getName());
    static public String FIND_ALBUM = "fr.flagadajones.MediaController.upnp.UpnpServerDevice.FIND_ALBUM";
    static public String LOADING = "fr.flagadajones.MediaController.upnp.UpnpServerDevice.LOADING";
    static public String LOADING_OK = "fr.flagadajones.MediaController.upnp.UpnpServerDevice.LOADING_OK";
    static public String BROWSE_OK = "fr.flagadajones.MediaController.upnp.UpnpServerDevice.BROWSE_OK";
    static public String LOADING_PISTE = "fr.flagadajones.MediaController.upnp.UpnpServerDevice.LOADING_PISTE";
    static public String LOADING_PISTE_OK = "fr.flagadajones.MediaController.upnp.UpnpServerDevice.LOADING_PISTE_OK";
    Service<Device, Service> contentDirectoryService = null;
    PisteDAO pisteDao = null;
    AlbumDAO albumDao = null;
    ArtisteDAO artisteDao = null;
    BroadcastReceiver mStatusListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (UpnpServerDevice.BROWSE_OK.equals(action)) {
                if (!listeNoeud.isEmpty()) {
                    String noeud = listeNoeud.remove(0);
                    browse(noeud,UpnpServerDevice.BROWSE_OK);
                } else {

                    Application.instance.unregisterReceiver(mStatusListener);
                    Application.activity.showToast("Chargement terminé", true);

                }
            }
        }
    };
    private List<String> listeNoeud = new ArrayList<String>();
    private List<Album> tmpAlbums = new ArrayList<Album>();
    private List<Piste> tmpPistes = new ArrayList<Piste>();

    public UpnpServerDevice() {

    }

    public UpnpServerDevice(Device device) {
        super(device);
        if (device.isFullyHydrated()) {
            if (contentDirectoryService == null)
                contentDirectoryService = device
                        .findService(new UDAServiceType("ContentDirectory"));
            // browseAlbums();

        }
    }

    public void setDevice(Device device) {
        super.setDevice(device);
        if (device.isFullyHydrated()) {
            if (contentDirectoryService == null)
                contentDirectoryService = device
                        .findService(new UDAServiceType("ContentDirectory"));
            if (selected)
                browseAlbums();

        }

    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected && device != null && device.isFullyHydrated())
            browseAlbums();
    }

    private void initDao() {
        if (pisteDao == null)
            pisteDao = new PisteDAO();
        if (albumDao == null)
            albumDao = new AlbumDAO();
        if (artisteDao == null)
            artisteDao = new ArtisteDAO();
    }

    public void browseAlbums() {
        listeNoeud.clear();
        IntentFilter f = new IntentFilter();
        f.addAction(UpnpServerDevice.BROWSE_OK);
        Application.instance.registerReceiver(mStatusListener,
                new IntentFilter(f));

        initDao();

        browse("0",UpnpServerDevice.BROWSE_OK);
    }

    public void loadPiste(String noeud) {
        Intent i = new Intent(UpnpServerDevice.LOADING_PISTE);
        Application.instance.sendBroadcast(i);
        initDao();
        browse(noeud,UpnpServerDevice.LOADING_PISTE_OK);

    }

    public void browse(String noeud, final String intentFilter) {
        
        Browse browseAction = new Browse(contentDirectoryService, noeud,
                BrowseFlag.DIRECT_CHILDREN) {

            @Override
            public void received(ActionInvocation actionInvocation,
                                 DIDLContent didl) {
                if (didl.getContainers() != null) {
                    for (Container iterable_element : didl.getContainers()) {
                        if (iterable_element instanceof MusicAlbum) {
                            createMusicAlbum((MusicAlbum) iterable_element);
                            //TODO : remettre pour chargement des pistes de l'album
                            //listeNoeud.add(iterable_element.getId());
//							log.warning("Ajoute 1 album " + listeNoeud.size());
                        } else if (iterable_element.getTitle()
                                .equals("Musique")) {
                            listeNoeud.add(iterable_element.getId());
                            break;
                        } else if (iterable_element.getTitle().equals("Album")) {
                            listeNoeud.add(iterable_element.getId());
                            break;
                        }
                    }
                    //log.warning("Insertion album ");
                    insertAlbums();
                    //log.warning("Insertion Ok");
                }
                if (didl.getItems() != null) {
                    for (Item iterable_element : didl.getItems()) {
                        if (iterable_element instanceof MusicTrack) {

                            createMusicTrack((MusicTrack) iterable_element);
                        }
                    }
                    insertPistes();

                }

                Intent i = new Intent(intentFilter);
                Application.instance.sendBroadcast(i);

            }

            @Override
            public void updateStatus(Status status) {
                // Called before and after loading the DIDL content
                // Application.activity.showToast(status.toString(), true);
                if (status.toString().equals("LOADING")) {
                    // Intent i = new Intent(UpnpServerDevice.LOADING);
                    // Application.instance.sendBroadcast(i);
                } else if (status.toString().equals("OK")) {

                    // i = new Intent(UpnpServerDevice.LOADING_OK);
                    // i.putExtra("artiste", album.id);
                    // i.putExtra("album", album.id);
                    // Application.instance.sendBroadcast(i);
                }

            }

            @Override
            public void failure(ActionInvocation invocation,
                                UpnpResponse operation, String defaultMsg) {
                Application.activity.showToast(defaultMsg, true);
                Intent i = new Intent(UpnpServerDevice.BROWSE_OK);
                Application.instance.sendBroadcast(i);
            }
        };
        
        
        UpnpDeviceManager.getInstance().upnpService.getControlPoint().execute(browseAction);
    }

    private void createMusicTrack(MusicTrack musiqueTrack) {
        Piste piste = new Piste();
        // une piste ne change pas d'album donc on peu le faire qu'une fois
        piste.upnpId = musiqueTrack.getId();

        piste.albumId = musiqueTrack.getParentID();

        piste.nom = musiqueTrack.getTitle();
        // piste.duree=musiqueTrack.getDuration();
        for (Res resource : musiqueTrack.getResources()) {
            piste.duree = resource.getDuration();
            piste.url = resource.getValue();
        }

        tmpPistes.add(piste);

    }

    private void createMusicAlbum(MusicAlbum musiqueAlbum) {
        try {
            Album album = new Album();
            album.upnpId = musiqueAlbum.getId();

            Artiste artiste = artisteDao.getArtiste(musiqueAlbum.getCreator());
            int artisteId = -1;
            if (artiste == null) {
                artiste = artisteDao.insertArtiste(new Artiste(0, musiqueAlbum
                        .getCreator(), 1));
            } else {
                artisteId = artiste.getId();
            }
            album.artiste = artiste;
            album.artisteId = artiste.getId();

            album.nom = musiqueAlbum.getTitle();
            album.nbTracks = musiqueAlbum.getChildCount();
            if (musiqueAlbum.getFirstAlbumArtURI() != null)
                // album.icone = ((RemoteDevice)
                // device).normalizeURI(musiqueAlbum.getFirstAlbumArtURI()).toString();
                album.icone = ((RemoteDevice) device).normalizeURI(
                        URI.create(musiqueAlbum.getFirstAlbumArtURI().getPath()
                                + "?scale=160x160")).toString();

            tmpAlbums.add(album);

            // Intent i = new
            // Intent(UpnpServerDevice.FIND_ALBUM);
            // i.putExtra("artiste", album.id);
            // i.putExtra("album", album.id);
            // Application.instance.sendBroadcast(i);
        } catch (RuntimeException e) {
            log.warning("erreur");
            int i = 0;
            // TODO: handle exception
        }
    }

    public void insertAlbums() {
        if (tmpAlbums == null || tmpAlbums.size() == 0)
            return;
        // Application.activity.showToast("Insert Albums", true);
        // The InsertHelper needs to have the db instance + the name of the
        // table where you want to add the data
        SQLiteDatabase database = MySQLOpenHelper.instance.getBaseDonnees();
        InsertHelper ih = new InsertHelper(database,
                MySQLOpenHelper.TABLE_ALBUMS);

        final int albumArt = ih
                .getColumnIndex(MySQLOpenHelper.COLONNE_ALBUM_ALBUM_ART);
        final int artisteId = ih
                .getColumnIndex(MySQLOpenHelper.COLONNE_ALBUM_ARTISTE_ID);
        final int albumId = ih.getColumnIndex(MySQLOpenHelper.COLONNE_ALBUM_ID);
        final int nbTrack = ih
                .getColumnIndex(MySQLOpenHelper.COLONNE_ALBUM_NB_TRACK);
        final int albumNom = ih
                .getColumnIndex(MySQLOpenHelper.COLONNE_ALBUM_NOM);
        final int ordre = ih
                .getColumnIndex(MySQLOpenHelper.COLONNE_ALBUM_ORDER);

        Collections.sort(tmpAlbums);

        database.setLockingEnabled(false);
        int number = 1;
        try {
            for (Album album : tmpAlbums) {
                ih.prepareForReplace();
                ih.bind(albumArt, album.icone);
                ih.bind(artisteId, album.artisteId);
                ih.bind(albumId, album.upnpId);
                ih.bind(nbTrack, album.nbTracks);
                ih.bind(albumNom, album.nom);
                ih.bind(ordre, number);

                ih.execute();
                number++;
            }
        } catch (Exception e) {
            log.warning("erreur");
            e.printStackTrace();
        } finally {
            if (ih != null)
                ih.close();
            database.setLockingEnabled(true);
        }
        tmpAlbums.clear();

        // Application.activity.showToast("Albums OK", true);
        // Application.instance.unregisterReceiver(mStatusListener);
        Intent i = new Intent(UpnpServerDevice.FIND_ALBUM);
        Application.instance.sendBroadcast(i);

    }

    public void insertPistes() {
        if (tmpPistes == null || tmpPistes.size() == 0)
            return;
   //     Application.activity.showToast("Insert Pistes", true);
        // The InsertHelper needs to have the db instance + the name of the
        // table where you want to add the data
        SQLiteDatabase database = MySQLOpenHelper.instance.getBaseDonnees();
        InsertHelper ih = new InsertHelper(database,
                MySQLOpenHelper.TABLE_PISTES);

        final int albumId = ih
                .getColumnIndex(MySQLOpenHelper.COLONNE_PISTE_ALBUM_ID);
        final int duree = ih
                .getColumnIndex(MySQLOpenHelper.COLONNE_PISTE_DUREE);
        final int pisteId = ih.getColumnIndex(MySQLOpenHelper.COLONNE_PISTE_ID);
        final int nom = ih.getColumnIndex(MySQLOpenHelper.COLONNE_PISTE_NOM);
        final int url = ih.getColumnIndex(MySQLOpenHelper.COLONNE_PISTE_URL);

        database.setLockingEnabled(false);
        try {
            for (Piste piste : tmpPistes) {
                ih.prepareForReplace();
                ih.bind(albumId, piste.albumId);
                ih.bind(duree, piste.duree);
                ih.bind(pisteId, piste.upnpId);
                ih.bind(nom, piste.nom);
                ih.bind(url, piste.url);
                ih.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ih != null)
                ih.close();
            database.setLockingEnabled(true);
        }
        tmpPistes.clear();
      //   Application.activity.showToast("Pistes OK", true);
    }

}
