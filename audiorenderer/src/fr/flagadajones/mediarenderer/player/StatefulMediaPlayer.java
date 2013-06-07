package fr.flagadajones.mediarenderer.player;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.util.Log;
import fr.fladajonesjones.media.model.Piste;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A subclass of android.media.MediaPlayer which provides methods for state-management, data-source management, etc.
 *
 * @author rootlicker http://speakingcode.com
 */
public class StatefulMediaPlayer extends android.media.MediaPlayer implements
        android.media.MediaPlayer.OnPreparedListener, OnErrorListener, OnCompletionListener {

    final private static Logger log = Logger.getLogger(StatefulMediaPlayer.class.getName());

    /**
     * Set of states for StatefulMediaPlayer:<br>
     * EMPTY, CREATED, PREPARED, STARTED, PAUSED, STOPPED, ERROR
     *
     * @author rootlicker
     */
    public enum MPStates {
        IDLE, INITIALIZED, PREPARING, PREPARED, STARTED, PAUSED, STOPPED, ERROR, PLAYBACKCOMPLETED, END
    }

    private OnPreparedListener onPreparedListener;
    private OnErrorListener onErrorListener;
    private OnCompletionListener onCompletionListener;
    private MPStates mState;

    @Override
    public void setOnPreparedListener(OnPreparedListener listener) {
        onPreparedListener = listener;
        super.setOnPreparedListener(this);
    }

    @Override
    public void setOnErrorListener(OnErrorListener listener) {
        onErrorListener = listener;

        super.setOnErrorListener(this);
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener listener) {
        onCompletionListener = listener;
        super.setOnCompletionListener(this);
    }

    ;

    /**
     * Sets a StatefulMediaPlayer's data source as the provided StreamStation
     *
     * @param audioItem the StreamStation to set as the data source
     */
    public void setAudioItem(Piste audioItem) {
        try {
            reset();
            setDataSource(audioItem.url);
            setState(MPStates.INITIALIZED);
        } catch (Exception e) {
            Log.e("StatefulMediaPlayer", "setDataSource failed");
            setState(MPStates.ERROR);
        }
    }

    /**
     * Instantiates a StatefulMediaPlayer object.
     */
    public StatefulMediaPlayer() {
        super();
        setState(MPStates.IDLE);
    }

    /**
     * Instantiates a StatefulMediaPlayer object with the Audio Stream Type set to STREAM_MUSIC and the provided
     * StreamStation's URL as the data source.
     *
     * @param audioItem The StreamStation to use as the data source
     */
    public StatefulMediaPlayer(Piste audioItem) {
        super();
        this.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            setDataSource(audioItem.url);
            setState(MPStates.INITIALIZED);
        } catch (Exception e) {
            Log.e("StatefulMediaPlayer", "setDataSourceFailed");
            setState(MPStates.ERROR);
        }
    }

    @Override
    public void reset() {
        super.reset();
        setState(MPStates.IDLE);
    }

    @Override
    public void start() {
        super.start();
        setState(MPStates.STARTED);
    }

    @Override
    public void pause() {

        super.pause();
        setState(MPStates.PAUSED);

    }

    @Override
    public void stop() {
        super.stop();
        setState(MPStates.STOPPED);
    }

    @Override
    public void release() {
        super.release();
        setState(MPStates.END);
    }

    @Override
    public void prepare() throws IOException, IllegalStateException {
        super.prepare();
        setState(MPStates.PREPARED);
    }

    @Override
    public void prepareAsync() throws IllegalStateException {
        super.prepareAsync();
        setState(MPStates.PREPARING);
    }

    public MPStates getState() {
        return mState;
    }

    // public void next() {
    //
    // }
    //
    // public void prev() {
    //
    // }

    /**
     * @param state the state to set
     */
    public void setState(MPStates state) {
        log.log(Level.WARNING, state.toString());
        this.mState = state;
    }

    public boolean isInitialized() {
        return (mState == MPStates.INITIALIZED);
    }

    public boolean isIdle() {
        return (mState == MPStates.IDLE);
    }

    public boolean isStopped() {
        return (mState == MPStates.STOPPED);
    }

    public boolean isStarted() {
        return (mState == MPStates.STARTED || this.isPlaying());
    }

    public boolean isPaused() {
        return (mState == MPStates.PAUSED);
    }

    public boolean isPrepared() {
        return (mState == MPStates.PREPARED);
    }

    public boolean isPreparing() {
        return (mState == MPStates.PREPARING);
    }

    public boolean isPlaybackCompleted() {
        return (mState == MPStates.PLAYBACKCOMPLETED);
    }

    public boolean isEnd() {
        return (mState == MPStates.END);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        setState(MPStates.PREPARED);
        onPreparedListener.onPrepared(mp);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        setState(MPStates.PLAYBACKCOMPLETED);
        onCompletionListener.onCompletion(mp);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        setState(MPStates.ERROR);
        return onErrorListener.onError(mp, what, extra);

    }
}
