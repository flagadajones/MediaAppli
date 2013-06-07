package fr.fladajonesjones.MediaControler.upnp;

import com.squareup.otto.Subscribe;
import fr.fladajonesjones.MediaControler.Application;
import fr.fladajonesjones.MediaControler.database.AlbumDAO;
import fr.fladajonesjones.MediaControler.database.ArtisteDAO;
import fr.fladajonesjones.MediaControler.database.PisteDAO;
import fr.fladajonesjones.MediaControler.events.*;
import fr.fladajonesjones.MediaControler.manager.UpnpDeviceManager;
import fr.fladajonesjones.media.model.Album;
import fr.fladajonesjones.media.model.Artiste;
import fr.fladajonesjones.media.model.Piste;
import fr.flagadajones.media.util.BusManager;
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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class UpnpServerDevice extends UpnpDevice {
    private static final Logger log = Logger.getLogger(UpnpServerDevice.class.getName());
    Service<Device, Service> contentDirectoryService = null;

    boolean browsing = false;

    PisteDAO pisteDao = null;
    AlbumDAO albumDao = null;
    ArtisteDAO artisteDao = null;

    @Subscribe
    public void onBrowseOk(UpnpServerBrowseOkEvent event) {
        if (!listeNoeud.isEmpty()) {
            String noeud = listeNoeud.remove(0);
            browse(noeud, new UpnpServerBrowseOkEvent());
        } else {

            BusManager.getInstance().unregister(this);
            Application.activity.showToast("Chargement terminé", true);

        }
    }

    private List<String> listeNoeud = new ArrayList<String>();
    private List<Album> tmpAlbums = new ArrayList<Album>();
    private List<Piste> tmpPistes = new ArrayList<Piste>();

    public UpnpServerDevice() {

    }

    public UpnpServerDevice(Device device) {
        super(device);
        if (device.isFullyHydrated()) {
            if (contentDirectoryService == null)
                contentDirectoryService = device.findService(new UDAServiceType("ContentDirectory"));
            // browseAlbums();

        }
    }

    public void setDevice(Device device) {
        super.setDevice(device);
        if (device.isFullyHydrated()) {
            if (contentDirectoryService == null)
                contentDirectoryService = device.findService(new UDAServiceType("ContentDirectory"));
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
        if (!browsing) {
            browsing = true;
            listeNoeud.clear();
            BusManager.getInstance().register(this);

            initDao();

            browse("0", new UpnpServerBrowseOkEvent());
        }
    }

    public void loadPiste(String noeud) {
        BusManager.getInstance().post(new UpnpServerLoadingPisteEvent());
        initDao();
        browse(noeud, new UpnpServerLoadingPisteOkEvent());

    }

    public void browse(String noeud, final UpnpServerEvent event) {

        Browse browseAction = new Browse(contentDirectoryService, noeud, BrowseFlag.DIRECT_CHILDREN) {

            @Override
            public void received(ActionInvocation actionInvocation, DIDLContent didl) {
                if (didl.getContainers() != null) {
                    for (Container iterable_element : didl.getContainers()) {
                        if (iterable_element instanceof MusicAlbum) {
                            createMusicAlbum((MusicAlbum) iterable_element);
                            // TODO : remettre pour chargement des pistes de l'album
                            // listeNoeud.add(iterable_element.getId());
                            // log.warning("Ajoute 1 album " + listeNoeud.size());
                        } else if (iterable_element.getTitle().equals("Musique")) {
                            listeNoeud.add(iterable_element.getId());
                            break;
                        } else if (iterable_element.getTitle().equals("Album")) {
                            listeNoeud.add(iterable_element.getId());
                            break;
                        }
                    }
                    // log.warning("Insertion album ");
                    albumDao.insertAlbums(tmpAlbums);
                    BusManager.getInstance().post(new UpnpServerFindAlbumEvent());

                    // log.warning("Insertion Ok");
                }
                if (didl.getItems() != null) {
                    for (Item iterable_element : didl.getItems()) {
                        if (iterable_element instanceof MusicTrack) {

                            createMusicTrack((MusicTrack) iterable_element);
                        }
                    }
                    pisteDao.insertPistes(tmpPistes);

                }

                BusManager.getInstance().post(event);

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
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                Application.activity.showToast(defaultMsg, true);
                // FIXME : ajouter un evenement ERROR
                BusManager.getInstance().post(new UpnpServerBrowseOkEvent());

            }
        };

        UpnpDeviceManager.getInstance().upnpService.getControlPoint().execute(browseAction);
    }

    private void createMusicTrack(MusicTrack musiqueTrack) {
        Piste piste = new Piste();
        // une piste ne change pas d'album donc on peu le faire qu'une fois
        piste.upnpId = musiqueTrack.getId();

        piste.albumId = musiqueTrack.getParentID();

        piste.titre = musiqueTrack.getTitle();
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
                artiste = artisteDao.insertArtiste(new Artiste(0, musiqueAlbum.getCreator(), 1));
            } else {
                artisteId = artiste.getId();
            }
            album.artiste = artiste;
            album.artisteId = artiste.getId();

            album.titre = musiqueAlbum.getTitle();
            album.nbTracks = musiqueAlbum.getChildCount();
            if (musiqueAlbum.getFirstAlbumArtURI() != null)
                // album.icone = ((RemoteDevice)
                // device).normalizeURI(musiqueAlbum.getFirstAlbumArtURI()).toString();
                album.albumArt = ((RemoteDevice) device).normalizeURI(
                        URI.create(musiqueAlbum.getFirstAlbumArtURI().getPath() + "?scale=250x250")).toString();

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

}
