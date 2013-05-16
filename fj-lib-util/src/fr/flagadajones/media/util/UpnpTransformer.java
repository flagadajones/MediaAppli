package fr.flagadajones.media.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.fladajonesjones.media.model.Radio;
import org.fourthline.cling.support.contentdirectory.DIDLParser;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.container.MusicAlbum;
import org.fourthline.cling.support.model.item.AudioItem;
import org.fourthline.cling.support.model.item.MusicTrack;

import fr.fladajonesjones.media.model.Album;
import fr.fladajonesjones.media.model.Musique;
import fr.fladajonesjones.media.model.Piste;

public class UpnpTransformer {
	final private static Logger log = Logger.getLogger(UpnpTransformer.class
			.getName());

	public static Musique metaDataToMusique(String metaData) {
		if (metaData != null) {
			DIDLContent content = null;
			try {
				content = new DIDLParser().parse(metaData);
			} catch (Exception e1) {

				log.log(Level.SEVERE, e1.getMessage(), e1);
				return null;
			}

			MusicAlbum albumUpnp = null;
			AudioItem trackUpnp = null;

			if (content.getFirstContainer() != null) {
				albumUpnp = (MusicAlbum) content.getFirstContainer();
				MusicTrack[] tracksUpnp = albumUpnp.getMusicTracks();

				Album album = new Album();
				album.upnpId = albumUpnp.getId();
				album.nbTracks = tracksUpnp.length;
				album.titre = albumUpnp.getTitle();
				List<URI> listeAlbumArt = albumUpnp
						.getPropertyValues(DIDLObject.Property.UPNP.ALBUM_ART_URI.class);
				for (URI uri2 : listeAlbumArt) {
					album.albumArt = uri2.toString();
				}

				for (int i = 0; i < tracksUpnp.length; i++) {
					MusicTrack pisteUpnp = tracksUpnp[i];
					Piste piste = new Piste();
					piste.titre = pisteUpnp.getTitle();
					piste.artiste = pisteUpnp.getCreator();

					piste.albumArt = album.albumArt;

					for (Res uri2 : pisteUpnp.getResources()) {
						piste.url = uri2.getValue();
						if (uri2.getDuration() != null
								|| !uri2.getDuration().equals(""))
							piste.duree = uri2.getDuration();
					}
					album.addPiste(piste);

				}
				return album;
			}
			if (content.getItems().size() != 0) {
				trackUpnp = (AudioItem) content.getItems().get(0);
				Piste piste = new Piste();
				piste.titre = trackUpnp.getTitle();
				piste.artiste = trackUpnp.getCreator();

				List<URI> listeAlbumArt = trackUpnp
						.getPropertyValues(DIDLObject.Property.UPNP.ALBUM_ART_URI.class);
				for (URI uri2 : listeAlbumArt) {
					piste.albumArt = uri2.toString();
				}
				for (Res uri2 : trackUpnp.getResources()) {
					piste.url = uri2.getValue();
					piste.duree = uri2.getDuration();

				}
				return piste;
			}

		} else {
			try {
				// FIXME : remettre ca en place en cas de lecture d'un seul
				// element
				// mMediaPlayerService.mMediaPlayer.setDataSource(uri.toString());
			} catch (Exception e) {
				log.log(Level.SEVERE, e.getMessage(), e);

			}
		}
		return null;
	}

	public static String albumToMetaData(Album album) {
		String metaData = "NO METADATA";
		DIDLContent didl = new DIDLContent();

		MusicAlbum albumUpnp = new MusicAlbum();
		// item.setRadioBand(radio.nom);
		albumUpnp.setTitle(album.titre);
		albumUpnp.setId(album.upnpId);
		albumUpnp.setParentID("0");
		try {
			if (album.albumArt != null)
				albumUpnp
						.addProperty(new DIDLObject.Property.UPNP.ALBUM_ART_URI(
								new URI(album.albumArt)));
		} catch (URISyntaxException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}

		// item.setRadioCallSign(radio.nom);
		List<MusicTrack> list = new ArrayList<MusicTrack>();

		for (Piste piste : album.getPistes()) {
			list.add(pisteToMetaData(piste, album.titre));
		}
		albumUpnp.addMusicTracks(list);
		didl.addContainer(albumUpnp);

		try {
			if (album.albumArt != null)
				albumUpnp
						.addProperty(new DIDLObject.Property.UPNP.ALBUM_ART_URI(
								new URI(album.albumArt)));
			metaData = new DIDLParser().generate(didl, true);
		} catch (Exception e) {
			// Toast.makeText(Application.instance, e1.getMessage(),
			// Toast.LENGTH_LONG).show();
			log.log(Level.SEVERE, e.getMessage(), e);
			metaData = "NO METADATA";

		}
		return metaData;
		// }
	}

	public static String pisteToMetaData(Piste piste) {
		String metaData = "NO METADATA";
		DIDLContent didl = new DIDLContent();

		didl.addItem(pisteToMetaData(piste, "No Name"));

		try {
			metaData = new DIDLParser().generate(didl, true);
		} catch (Exception e) {
			// Toast.makeText(Application.instance, e1.getMessage(),
			// Toast.LENGTH_LONG).show();
			log.log(Level.SEVERE, e.getMessage(), e);
			metaData = "NO METADATA";

		}
		return metaData;
		// }
	}
    public static String radioToMetaData(Radio radio) {
        String metaData = "NO METADATA";
        DIDLContent didl = new DIDLContent();

        didl.addItem(radioMetaData(radio));

        try {
            metaData = new DIDLParser().generate(didl, true);
        } catch (Exception e) {
            // Toast.makeText(Application.instance, e1.getMessage(),
            // Toast.LENGTH_LONG).show();
            log.log(Level.SEVERE, e.getMessage(), e);
            metaData = "NO METADATA";

        }
        return metaData;
        // }
    }

    private static MusicTrack radioMetaData(Radio radio) {
        MusicTrack radioUpnp = new MusicTrack();

        radioUpnp.setId(radio.upnpId);
        radioUpnp.setParentID("1");
        radioUpnp.setRestricted(false);
        radioUpnp.setTitle(radio.titre);
        try {
            if (radio.albumArt != null)
                radioUpnp
                        .addProperty(new DIDLObject.Property.UPNP.ALBUM_ART_URI(
                                new URI(radio.albumArt)));
        } catch (URISyntaxException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        radioUpnp.addResource(new Res("audio/mp3", null,null, null,
                radio.url));

        return radioUpnp;
    }

	private static MusicTrack pisteToMetaData(Piste piste, String albumName) {
		MusicTrack pisteUpnp = new MusicTrack();
		pisteUpnp.setAlbum(albumName);
		pisteUpnp.setId(piste.upnpId);
		pisteUpnp.setParentID(piste.albumId);
		pisteUpnp.setRestricted(false);
		pisteUpnp.setTitle(piste.titre);
		try {
			if (piste.albumArt != null)
				pisteUpnp
						.addProperty(new DIDLObject.Property.UPNP.ALBUM_ART_URI(
								new URI(piste.albumArt)));
		} catch (URISyntaxException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
		pisteUpnp.addResource(new Res("audio/mp3", null, piste.duree, null,
				piste.url));

		return pisteUpnp;
	}

	public static String calculTotalTime(List<Piste> lstPistes) {
		Long time = 0L;
		for (Piste piste : lstPistes) {
			time += StringUtils.makeLongFromStringTime(piste.duree);
		}

		return StringUtils.makeTimeString(time);
	}
}
