package fr.flagadajones.mediarenderer.upnp.service;

import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;
import fr.fladajonesjones.media.model.Album;
import fr.fladajonesjones.media.model.Musique;
import fr.fladajonesjones.media.model.Piste;
import fr.flagadajones.media.util.BusManager;
import fr.flagadajones.media.util.StringUtils;
import fr.flagadajones.media.util.UpnpTransformer;
import fr.flagadajones.mediarenderer.events.frommediaservice.PlayerSongUpdateEvent;
import fr.flagadajones.mediarenderer.events.frommediaservice.PlayerUpdatePosEvent;
import fr.flagadajones.mediarenderer.events.fromupnpservice.*;
import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.avtransport.AVTransportErrorCode;
import org.fourthline.cling.support.avtransport.AVTransportException;
import org.fourthline.cling.support.avtransport.AbstractAVTransportService;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.support.model.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyAVTransportService extends AbstractAVTransportService {

    private static final Logger log = Logger.getLogger(MyAVTransportService.class.getName());

    private DeviceCapabilities deviceCapabilities = new DeviceCapabilities(
            new StorageMedium[]{StorageMedium.NETWORK});
    private MediaInfo mediaInfo = new MediaInfo();
    private PositionInfo positionInfo = new PositionInfo();
    private TransportAction[] transportAction = new TransportAction[0];
    private TransportSettings transportSettings = new TransportSettings(PlayMode.NORMAL);
    private TransportState transportState = TransportState.NO_MEDIA_PRESENT;
    private TransportStatus transportStatus = TransportStatus.OK;
    PlayerSongUpdateEvent eventUpdate;
    private String metaData = "";

    private boolean firstPiste=false;
    private boolean lastPiste=false;
    public MyAVTransportService(LastChange lastChange) {
        super(lastChange);
        BusManager.getInstance().register(this);

    }



    // /////////////////////////////////////////////////////////////////////////////////////
    // / OTTO
    // /////////////////////////////////////////////////////////////////////////////////////
    @Subscribe
    public void onUpdateSongInfo(PlayerSongUpdateEvent event) {
        this.eventUpdate = event;

        if (event.playListSize == 0) {
            mediaInfo = new MediaInfo();
            positionInfo = new PositionInfo();
            firstPiste=true;
            lastPiste=true;
        } else {
            if(event.trackPosition==0){
                firstPiste=true;
            }else{
                firstPiste=false;
            }
            if(event.trackPosition==event.playListSize-1){
                lastPiste=true;
            }else{
                lastPiste=false;
            }
            mediaInfo = new MediaInfo(event.mediaUrl, metaData, new UnsignedIntegerFourBytes(event.playListSize),

                    event.mediaDuration, StorageMedium.NETWORK);

            //positionInfo = new PositionInfo(event.trackPosition,metaData,event.trackUrl);
        }
        defineTransportAction();
    }

    @Subscribe
    public void onUpdatePositionInfo(PlayerUpdatePosEvent event) {
        int timePos = event.pos / 1000;
        String time = StringUtils.makeTimeString(timePos);
        positionInfo = new PositionInfo(eventUpdate.trackPosition, eventUpdate.trackDuration, metaData, eventUpdate.trackUrl, time, time, timePos, timePos);


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

            if (!seekMode.equals(SeekMode.REL_TIME) && !seekMode.equals(SeekMode.TRACK_NR)) {
                throw new IllegalArgumentException();
            }

            if (seekMode.equals(SeekMode.TRACK_NR))
                BusManager.getInstance().post(new PlayerSeekEvent(0, Integer.valueOf(target)));

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
    private void nextOrPrev(List<TransportAction> liste){

        if (!firstPiste)
            liste.add(TransportAction.Previous);
        if(!lastPiste)
            liste.add(TransportAction.Next);

    }

    private void defineTransportAction() {
        List<TransportAction> transportList=new ArrayList<TransportAction>();
        switch (transportState) {
            case NO_MEDIA_PRESENT:
                //transportAction = transportList.toArray(new TransportAction[0]);
                break;
            case PAUSED_PLAYBACK:
                transportList=new ArrayList<TransportAction>(Arrays.asList(new TransportAction[]{TransportAction.Stop, TransportAction.Play,TransportAction.Seek}));
                nextOrPrev(transportList);
                break;
            case PLAYING:
                transportList=new ArrayList<TransportAction>(Arrays.asList(new TransportAction[]{TransportAction.Stop,TransportAction.Pause,TransportAction.Seek}));
                nextOrPrev(transportList);
                break;
            case STOPPED:
                transportList=new ArrayList<TransportAction>(Arrays.asList(new TransportAction[]{TransportAction.Play,TransportAction.Seek}));
                nextOrPrev(transportList);
            default:
                transportList=new ArrayList<TransportAction>(Arrays.asList(new TransportAction[]{TransportAction.Stop, TransportAction.Play,
                        TransportAction.Pause,TransportAction.Seek}));
                nextOrPrev(transportList);
                break;
        }
        transportAction=transportList.toArray(new TransportAction[0]);
        getLastChange().setEventedValue(0, new AVTransportVariable.CurrentTransportActions(transportAction));
        BusManager.getInstance().post(produceUpnpRendererTransportActionEvent());
    }

    @Produce
    public UpnpRendererTransportActionEvent produceUpnpRendererTransportActionEvent(){
    return new UpnpRendererTransportActionEvent(transportAction);
    }

    @Override
    protected void finalize() throws Throwable {

        super.finalize();
        BusManager.getInstance().unregister(this);
    }

    // ////////////////////////////////////////////////////////////////////////////
    // UPNP VARIABLES
    // ////////////////////////////////////////////////////////////////////////////

    @Override
    public UnsignedIntegerFourBytes[] getCurrentInstanceIds() {
        return new UnsignedIntegerFourBytes[]{new UnsignedIntegerFourBytes(0)};
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
