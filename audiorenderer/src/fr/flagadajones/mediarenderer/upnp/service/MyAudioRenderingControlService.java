package fr.flagadajones.mediarenderer.upnp.service;

import android.content.Context;
import android.media.AudioManager;
import fr.flagadajones.media.util.BusManager;
import fr.flagadajones.mediarenderer.Application;
import fr.flagadajones.mediarenderer.events.fromupnpservice.PlayerSetVolumeEvent;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.types.UnsignedIntegerTwoBytes;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.support.model.Channel;
import org.fourthline.cling.support.renderingcontrol.AbstractAudioRenderingControl;
import org.fourthline.cling.support.renderingcontrol.RenderingControlException;
import org.fourthline.cling.support.renderingcontrol.lastchange.ChannelMute;
import org.fourthline.cling.support.renderingcontrol.lastchange.ChannelVolume;
import org.fourthline.cling.support.renderingcontrol.lastchange.RenderingControlVariable;

public class MyAudioRenderingControlService extends AbstractAudioRenderingControl {
    private AudioManager audMgr = (AudioManager) Application.instance.getSystemService(Context.AUDIO_SERVICE);
    private boolean mute = false;

    public MyAudioRenderingControlService(LastChange lastChange) {
        super(lastChange);

    }

    @Override
    protected Channel[] getCurrentChannels() {
        return new Channel[]{Channel.Master};
    }

    @Override
    public UnsignedIntegerFourBytes[] getCurrentInstanceIds() {
        return new UnsignedIntegerFourBytes[]{new UnsignedIntegerFourBytes(0)};
    }

    @Override
    public boolean getMute(UnsignedIntegerFourBytes arg0, String arg1) throws RenderingControlException {
        return mute;
    }

    @Override
    public UnsignedIntegerTwoBytes getVolume(UnsignedIntegerFourBytes arg0, String arg1)
            throws RenderingControlException {
        int volume = audMgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        return new UnsignedIntegerTwoBytes(volume);
    }

    @Override
    public void setMute(UnsignedIntegerFourBytes arg0, String arg1, boolean mute) throws RenderingControlException {
        audMgr.setStreamMute(AudioManager.STREAM_MUSIC, mute);
        getLastChange().setEventedValue(0, new RenderingControlVariable.Mute(new ChannelMute(Channel.Master, mute)));
        this.mute = mute;

    }

    @Override
    public void setVolume(UnsignedIntegerFourBytes arg0, String arg1, UnsignedIntegerTwoBytes arg2)
            throws RenderingControlException {
        getLastChange().setEventedValue(0,
                new RenderingControlVariable.Volume(new ChannelVolume(Channel.Master, arg2.getValue().intValue())));
        BusManager.getInstance().post(new PlayerSetVolumeEvent(arg2.getValue().intValue()));
    }

}
