package fr.flagadajones.mediarenderer.upnp.service;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.avtransport.AVTransportErrorCode;
import org.fourthline.cling.support.avtransport.AVTransportException;
import org.fourthline.cling.support.avtransport.AbstractAVTransportService;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.support.model.DeviceCapabilities;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PlayMode;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.SeekMode;
import org.fourthline.cling.support.model.StorageMedium;
import org.fourthline.cling.support.model.TransportAction;
import org.fourthline.cling.support.model.TransportInfo;
import org.fourthline.cling.support.model.TransportSettings;
import org.fourthline.cling.support.model.TransportState;
import org.fourthline.cling.support.model.TransportStatus;

import com.squareup.otto.Subscribe;

import fr.fladajonesjones.media.model.Album;
import fr.fladajonesjones.media.model.Musique;
import fr.fladajonesjones.media.model.Piste;
import fr.flagadajones.media.util.BusManager;
import fr.flagadajones.media.util.UpnpTransformer;
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

public class MyAVTransportService extends AbstractAVTransportService {

    private static final Logger log = Logger.getLogger(MyAVTransportService.class.getName());

    private DeviceCapabilities deviceCapabilities = new DeviceCapabilities(
            new StorageMedium[] { StorageMedium.NETWORK });
    private MediaInfo mediaInfo = new MediaInfo();
    private PositionInfo positionInfo = new PositionInfo();
    private TransportAction[] transportAction = new TransportAction[0];
    private TransportSettings transportSettings = new TransportSettings(PlayMode.NORMAL);
    private TransportState transportState = TransportState.NO_MEDIA_PRESENT;
    private TransportStatus transportStatus = TransportStatus.OK;

    private String metaData = "";

    public MyAVTransportService(LastChange lastChange) {
        super(lastChange);

    }

    // /////////////////////////////////////////////////////////////////////////////////////
    // / OTTO
    // /////////////////////////////////////////////////////////////////////////////////////
    @Subscribe
    public void updateSongInfo(PlayerSongUpdateEvent event) {

        if (event.playListSize == 0) {
            mediaInfo = new MediaInfo();
            positionInfo = new PositionInfo();
        } else {
            mediaInfo = new MediaInfo(event.mediaUrl, metaData, new UnsignedIntegerFourBytes(event.playListSize),

            event.mediaDuration, StorageMedium.NETWORK);
            positionInfo = new PositionInfo(event.trackPosition,event.trackDuration,event.trackUrl,"00:00:00","00:00:00");
        }
    }

    // /////////////////////////////////////////////////////////////////////////////////////
    // / UPNP ACTIONS
    // /////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void play(UnsignedIntegerFourBytes instanceId, String speed) throws AVTransportException {
        changeTransportState(TransportState.PLAYING);
        BusManager.getInstance().post(new PlayerStartEvent());
    }

    @Override
    public void stop(UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        changeTransportState(TransportState.STOPPED);
        BusManager.getInstance().post(new PlayerStopEvent());
    }

    @Override
    public void pause(UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        changeTransportState(TransportState.PAUSED_PLAYBACK);
        BusManager.getInstance().post(new PlayerPauseEvent());
    }

    @Override
    public void previous(UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        BusManager.getInstance().post(new PlayerPrevEvent());
    }

    @Override
    public void next(UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        BusManager.getInstance().post(new PlayerNextEvent());
    }

    @Override
    public void seek(UnsignedIntegerFourBytes instanceId, String unit, String target) throws AVTransportException {
        SeekMode seekMode;
        try {
            seekMode = SeekMode.valueOrExceptionOf(unit);

            if (!seekMode.equals(SeekMode.REL_TIME)) {
                throw new IllegalArgumentException();
            }

            BusManager.getInstance().post(new PlayerSeekEvent(Integer.valueOf(target)));

        } catch (IllegalArgumentException ex) {
            log.log(Level.SEVERE, ex.getMessage(), ex);
            throw new AVTransportException(AVTransportErrorCode.SEEKMODE_NOT_SUPPORTED, "Unsupported seek mode: "
                    + unit);
        }
    }

    @Override
    public void setAVTransportURI(UnsignedIntegerFourBytes instanceId, String currentURI, String currentURIMetaData)
            throws AVTransportException {

        URI uri;
        try {
            uri = new URI(currentURI);

            // TODO: Check mime type of resource against supported types

            metaData = currentURIMetaData;

            Musique musique = UpnpTransformer.metaDataToMusique(currentURIMetaData);

            if (musique == null)
                return;

            if (musique instanceof Album) {
                BusManager.getInstance().post(new PlayerClearEvent());
                BusManager.getInstance().post(new PlayerPlayListEvent(((Album) musique).getPistes()));
                BusManager.getInstance().post(new PlayerInitializeEvent(0));
            } else if (musique instanceof Piste) {
                BusManager.getInstance().post(new PlayerInitializeEvent((Piste) musique));
            }

            getLastChange().setEventedValue(0, new AVTransportVariable.AVTransportURI(uri),
                    new AVTransportVariable.CurrentTrackURI(uri));

        } catch (Exception ex) {
            log.log(Level.SEVERE, ex.getMessage(), ex);
            throw new AVTransportException(ErrorCode.INVALID_ARGS, "Erreur");
        }

    }

    private void changeTransportState(TransportState transport) {
        transportState = transport;
        defineTransportAction();
        getLastChange().setEventedValue(0, new AVTransportVariable.TransportState(transportState));
    }

    private void defineTransportAction() {
        switch (transportState) {
        case NO_MEDIA_PRESENT:
            transportAction = new TransportAction[] {};
            break;
        case PAUSED_PLAYBACK:
            transportAction = new TransportAction[] { TransportAction.Stop, TransportAction.Play, TransportAction.Next,
                    TransportAction.Previous, TransportAction.Seek };
            break;
        case PLAYING:
            transportAction = new TransportAction[] { TransportAction.Stop, TransportAction.Pause,
                    TransportAction.Next, TransportAction.Previous, TransportAction.Seek };
            break;
        case STOPPED:
            // TODO ajouetr next/prev en fonction de la playlist
            transportAction = new TransportAction[] { TransportAction.Play, TransportAction.Next,
                    TransportAction.Previous, TransportAction.Seek };
        default:
            transportAction = new TransportAction[] { TransportAction.Stop, TransportAction.Play,
                    TransportAction.Pause, TransportAction.Next, TransportAction.Previous, TransportAction.Seek };
            break;
        }

        getLastChange().setEventedValue(0, new AVTransportVariable.CurrentTransportActions(transportAction));

    }

    // ////////////////////////////////////////////////////////////////////////////
    // UPNP VARIABLES
    // ////////////////////////////////////////////////////////////////////////////

    @Override
    public UnsignedIntegerFourBytes[] getCurrentInstanceIds() {
        return new UnsignedIntegerFourBytes[] { new UnsignedIntegerFourBytes(0) };
    }

    @Override
    protected TransportAction[] getCurrentTransportActions(UnsignedIntegerFourBytes arg0) throws Exception {
        return transportAction;
    }

    @Override
    public DeviceCapabilities getDeviceCapabilities(UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        return deviceCapabilities;
    }

    @Override
    public MediaInfo getMediaInfo(UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        return mediaInfo;
    }

    @Override
    public PositionInfo getPositionInfo(UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        return positionInfo;
    }

    @Override
    public TransportInfo getTransportInfo(UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        return new TransportInfo(transportState, transportStatus);
    }

    @Override
    public TransportSettings getTransportSettings(UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        return transportSettings;
    }

    // ////////////////////////////////////////////////////////////////////////////
    // / NOT IMPLEMENTED ////
    // ////////////////////////////////////////////////////////////////////////////

    @Override
    public void setNextAVTransportURI(UnsignedIntegerFourBytes instanceId, String nextURI, String nextURIMetaData)
            throws AVTransportException {
        log.info("### TODO: Not implemented: SetNextAVTransportURI");
    }

    @Override
    public void setPlayMode(UnsignedIntegerFourBytes instanceId, String newPlayMode) throws AVTransportException {
        log.info("### TODO: Not implemented: SetPlayMode");
    }

    @Override
    public void setRecordQualityMode(UnsignedIntegerFourBytes instanceId, String newRecordQualityMode)
            throws AVTransportException {
        log.info("### TODO: Not implemented: SetRecordQualityMode");
    }

    @Override
    public void record(UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        log.info("### TODO: Not implemented: Record");
    }

}
