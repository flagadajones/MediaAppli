package fr.flagadajones.mediarenderer.upnp.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.avtransport.AVTransportErrorCode;
import org.fourthline.cling.support.avtransport.AVTransportException;
import org.fourthline.cling.support.avtransport.AbstractAVTransportService;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable;
import org.fourthline.cling.support.contentdirectory.DIDLParser;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.DeviceCapabilities;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PlayMode;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.SeekMode;
import org.fourthline.cling.support.model.StorageMedium;
import org.fourthline.cling.support.model.TransportAction;
import org.fourthline.cling.support.model.TransportInfo;
import org.fourthline.cling.support.model.TransportSettings;
import org.fourthline.cling.support.model.TransportState;
import org.fourthline.cling.support.model.TransportStatus;
import org.fourthline.cling.support.model.container.MusicAlbum;
import org.fourthline.cling.support.model.item.AudioItem;
import org.fourthline.cling.support.model.item.MusicTrack;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import fr.flagadajones.mediarenderer.events.PlayerClearEvent;
import fr.flagadajones.mediarenderer.events.PlayerInitializeEvent;
import fr.flagadajones.mediarenderer.events.PlayerNextEvent;
import fr.flagadajones.mediarenderer.events.PlayerPauseEvent;
import fr.flagadajones.mediarenderer.events.PlayerPlayListEvent;
import fr.flagadajones.mediarenderer.events.PlayerPrevEvent;
import fr.flagadajones.mediarenderer.events.PlayerSeekEvent;
import fr.flagadajones.mediarenderer.events.PlayerSongUpdateEvent;
import fr.flagadajones.mediarenderer.events.PlayerStartEvent;
import fr.flagadajones.mediarenderer.events.PlayerStopEvent;
import fr.flagadajones.mediarenderer.util.BusManager;

public class MyAVTransportService extends AbstractAVTransportService {

	private DeviceCapabilities deviceCapabilities = new DeviceCapabilities(
			new StorageMedium[] { StorageMedium.NETWORK });
	private TransportSettings transportSettings = new TransportSettings(
			PlayMode.NORMAL);
	private TransportAction[] transportAction = new TransportAction[0];

	private MediaInfo mediaInfo = new MediaInfo();
	private PositionInfo positionInfo = new PositionInfo();
	// private MediaPlayerService mMediaPlayerService;
	private TransportState transportState = TransportState.NO_MEDIA_PRESENT;
	private TransportStatus transportStatus = TransportStatus.OK;

	private String metaData = "";

	// public MediaPlayerService getmMediaPlayer() {
	// return mMediaPlayerService;
	// }
	//
	// public void setmMediaPlayer(MediaPlayerService mMediaPlayer) {
	// this.mMediaPlayerService = mMediaPlayer;
	// }

	final private static Logger log = Logger
			.getLogger(MyAVTransportService.class.getName());

	public MyAVTransportService(LastChange lastChange) {
		super(lastChange);

	}

	@Override
	public void setAVTransportURI(UnsignedIntegerFourBytes instanceId,
			String currentURI, String currentURIMetaData)
			throws AVTransportException {

		URI uri;
		try {
			uri = new URI(currentURI);

			// if (currentURI.startsWith("http:")) {
			// try {
			// HttpFetch.validate(URIUtil.toURL(uri));
			// } catch (Exception ex) {
			// throw new AVTransportException(
			// AVTransportErrorCode.RESOURCE_NOT_FOUND,
			// ex.getMessage());
			// }
			// } else if (!currentURI.startsWith("file:")) {
			// throw new AVTransportException(ErrorCode.INVALID_ARGS,
			// "Only HTTP and file: resource identifiers are supported");
			// }

			// TODO: Check mime type of resource against supported types

			// TODO: DIDL fragment parsing and handling of currentURIMetaData
			setTransportURI(uri, currentURIMetaData);
		} catch (Exception ex) {
			log.log(Level.SEVERE, ex.getMessage(), ex);
			throw new AVTransportException(ErrorCode.INVALID_ARGS,
					"Erreur");
		}

	}

	@Override
	public MediaInfo getMediaInfo(UnsignedIntegerFourBytes instanceId)
			throws AVTransportException {
		return mediaInfo;
	}

	@Override
	public TransportInfo getTransportInfo(UnsignedIntegerFourBytes instanceId)
			throws AVTransportException {
		return new TransportInfo(transportState, transportStatus);
	}

	@Override
	public PositionInfo getPositionInfo(UnsignedIntegerFourBytes instanceId)
			throws AVTransportException {
		return positionInfo;
	}

	@Subscribe
	public void updateSongInfo(PlayerSongUpdateEvent event) {

		mediaInfo = new MediaInfo(event.mediaUrl, metaData,
				new UnsignedIntegerFourBytes(event.playListSize),
				event.mediaDuration, StorageMedium.NETWORK);
		positionInfo = new PositionInfo(event.trackPosition, metaData,
				event.trackUrl);
	}

	@Override
	public DeviceCapabilities getDeviceCapabilities(
			UnsignedIntegerFourBytes instanceId) throws AVTransportException {
		return deviceCapabilities;
	}

	@Override
	public TransportSettings getTransportSettings(
			UnsignedIntegerFourBytes instanceId) throws AVTransportException {
		return transportSettings;
	}

	@Override
	public void stop(UnsignedIntegerFourBytes instanceId)
			throws AVTransportException {
		changeTransportState(TransportState.STOPPED);
		BusManager.getInstance().post(new PlayerStopEvent());
	}

	@Override
	public void play(UnsignedIntegerFourBytes instanceId, String speed)
			throws AVTransportException {
		changeTransportState(TransportState.PLAYING);
		BusManager.getInstance().post(new PlayerStartEvent());
	}

	@Override
	public void pause(UnsignedIntegerFourBytes instanceId)
			throws AVTransportException {
		changeTransportState(TransportState.PAUSED_PLAYBACK);
		BusManager.getInstance().post(new PlayerPauseEvent());

	}

	private void changeTransportState(TransportState transport) {
		transportState = transport;
		defineTransportAction();
		getLastChange().setEventedValue(0,
				new AVTransportVariable.TransportState(transportState));
	}

	@Override
	public void record(UnsignedIntegerFourBytes instanceId)
			throws AVTransportException {
		// Not implemented
		log.info("### TODO: Not implemented: Record");
	}

	@Override
	public void seek(UnsignedIntegerFourBytes instanceId, String unit,
			String target) throws AVTransportException {
		SeekMode seekMode;
		try {
			seekMode = SeekMode.valueOrExceptionOf(unit);

			if (!seekMode.equals(SeekMode.REL_TIME)) {
				throw new IllegalArgumentException();
			}

			// final ClockTime ct =
			// ClockTime.fromSeconds(ModelUtil.fromTimeString(target));
			// if (player.getPipeline().getState().equals(State.PLAYING)) {
			// player.pause();
			// player.getPipeline().seek(ct);
			// player.play();
			// } else if (player.getPipeline().getState().equals(State.PAUSED))
			// {
			// player.getPipeline().seek(ct);
			// }

			BusManager.getInstance().post(
					new PlayerSeekEvent(Integer.valueOf(target)));

		} catch (IllegalArgumentException ex) {
			log.log(Level.SEVERE, ex.getMessage(), ex);
			throw new AVTransportException(
					AVTransportErrorCode.SEEKMODE_NOT_SUPPORTED,
					"Unsupported seek mode: " + unit);
		}
	}

	@Override
	public void next(UnsignedIntegerFourBytes instanceId)
			throws AVTransportException {
		BusManager.getInstance().post(new PlayerNextEvent());

	}

	@Override
	public void previous(UnsignedIntegerFourBytes instanceId)
			throws AVTransportException {
		BusManager.getInstance().post(new PlayerPrevEvent());

	}

	@Override
	public void setNextAVTransportURI(UnsignedIntegerFourBytes instanceId,
			String nextURI, String nextURIMetaData) throws AVTransportException {
		log.info("### TODO: Not implemented: SetNextAVTransportURI");
		// Not implemented
	}

	@Override
	public void setPlayMode(UnsignedIntegerFourBytes instanceId,
			String newPlayMode) throws AVTransportException {
		// Not implemented
		log.info("### TODO: Not implemented: SetPlayMode");
	}

	@Override
	public void setRecordQualityMode(UnsignedIntegerFourBytes instanceId,
			String newRecordQualityMode) throws AVTransportException {
		// Not implemented
		log.info("### TODO: Not implemented: SetRecordQualityMode");
	}

	public void setTransportURI(URI uri, String meta) {
		metaData = meta;
		// mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
		if (metaData != null) {
			DIDLContent content = null;
			try {
				content = new DIDLParser().parse(metaData);
			} catch (Exception e1) {

				e1.printStackTrace();
				return;
			}

			MusicAlbum album = null;

			AudioItem musique = null;

			if (content.getFirstContainer() != null) {
				album = (MusicAlbum) content.getFirstContainer();

			}
			if (content.getItems().size() != 0) {
				musique = (AudioItem) content.getItems().get(0);

			}

			if (album != null) {
				BusManager.getInstance().post(new PlayerClearEvent());

				// File fichier = Utils.DownloadFromUrl(uri.toString(),
				// "playlist.m3u");
				//
				// try {
				// FileInputStream fstream = new FileInputStream(fichier);
				// DataInputStream in = new DataInputStream(fstream);
				// BufferedReader br = new BufferedReader(new
				// InputStreamReader(in));
				// String strLine;
				// while ((strLine = br.readLine()) != null) {
				// Application.mediaPlayer.playlist.add(strLine);
				// }
				//
				// in.close();
				// } catch (Exception e) {// Catch exception if any
				// System.err.println("Error: " + e.getMessage());
				// }
				MusicTrack[] musiques = album.getMusicTracks();
				String albumArt = null;
				List<URI> listeAlbumArt = album
						.getPropertyValues(DIDLObject.Property.UPNP.ALBUM_ART_URI.class);
				for (URI uri2 : listeAlbumArt) {
					albumArt = uri2.toString();
				}

				List<fr.flagadajones.mediarenderer.AudioItem> playList = new ArrayList<fr.flagadajones.mediarenderer.AudioItem>();
				for (int i = 0; i < musiques.length; i++) {
					MusicTrack piste = musiques[i];
					fr.flagadajones.mediarenderer.AudioItem audioItem = new fr.flagadajones.mediarenderer.AudioItem();
					audioItem.title = piste.getTitle();
					audioItem.artiste = piste.getAlbum();

					audioItem.albumArt = albumArt;

					for (Res uri2 : piste.getResources()) {
						audioItem.url = uri2.getValue();
						audioItem.duration = uri2.getDuration();

					}
					playList.add(audioItem);

				}
				BusManager.getInstance()
						.post(new PlayerPlayListEvent(playList));

				// for (URI uri2 : listeAlbumArt) {
				// Application.mediaPlayer.albumArtUrl = uri2.toString();
				// }
				BusManager.getInstance().post(new PlayerInitializeEvent(0));
			}

			if (musique != null) {

				fr.flagadajones.mediarenderer.AudioItem audioItem = new fr.flagadajones.mediarenderer.AudioItem();
				audioItem.title = musique.getTitle();
				audioItem.artiste = musique.getCreator();

				List<URI> listeAlbumArt = musique
						.getPropertyValues(DIDLObject.Property.UPNP.ALBUM_ART_URI.class);
				for (URI uri2 : listeAlbumArt) {
					audioItem.albumArt = uri2.toString();
				}
				for (Res uri2 : musique.getResources()) {
					audioItem.url = uri2.getValue();
					audioItem.duration = uri2.getDuration();

				}
				BusManager.getInstance().post(
						new PlayerInitializeEvent(audioItem));
				// mMediaPlayerService.initializePlayer(audioItem);
			}

			// try {
			// mMediaPlayerService.mMediaPlayer
			// .setDataSource(mMediaPlayerService.mMediaPlayer
			// .getAudioItem().url);
			// } catch (Exception e) {
			//
			// e.printStackTrace();
			// }

		} else {
			try {
				// FIXME : remettre ca en place en cas de lecture d'un seul
				// element
				// mMediaPlayerService.mMediaPlayer.setDataSource(uri.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.log(Level.SEVERE, e.getMessage(), e);

			}
		}

		// If you can, you should find and set the duration of the track here!

		// It's up to you what "last changes" you want to announce to event
		// listeners
		getLastChange().setEventedValue(0/* transport.getInstanceId() */,
				new AVTransportVariable.AVTransportURI(uri),
				new AVTransportVariable.CurrentTrackURI(uri));

	}

	private void defineTransportAction() {

		switch (transportState) {
		case NO_MEDIA_PRESENT:
			transportAction = new TransportAction[] {};
			break;
		case PAUSED_PLAYBACK:
			transportAction = new TransportAction[] { TransportAction.Stop,
					TransportAction.Play, TransportAction.Next,
					TransportAction.Previous, TransportAction.Seek };
			break;
		case PLAYING:
			transportAction = new TransportAction[] { TransportAction.Stop,
					TransportAction.Pause, TransportAction.Next,
					TransportAction.Previous, TransportAction.Seek };
			break;
		case STOPPED:
			// TODO ajouetr next/prev en fonction de la playlist
			transportAction = new TransportAction[] { TransportAction.Play,
					TransportAction.Next, TransportAction.Previous,
					TransportAction.Seek };
		default:
			transportAction = new TransportAction[] { TransportAction.Stop,
					TransportAction.Play, TransportAction.Pause,
					TransportAction.Next, TransportAction.Previous,
					TransportAction.Seek };
			break;
		}

		getLastChange()
				.setEventedValue(
						0,
						new AVTransportVariable.CurrentTransportActions(
								transportAction));

	}

	@Override
	public UnsignedIntegerFourBytes[] getCurrentInstanceIds() {

		return new UnsignedIntegerFourBytes[] { new UnsignedIntegerFourBytes(0) };
	}

	@Override
	protected TransportAction[] getCurrentTransportActions(
			UnsignedIntegerFourBytes arg0) throws Exception {
		return transportAction;
	}

}
