package fr.flagadajones.mediarenderer.services;

import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.IBinder;
import android.util.Log;

import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import fr.flagadajones.mediarenderer.AudioItem;
import fr.flagadajones.mediarenderer.activity.MainActivity;
import fr.flagadajones.mediarenderer.events.PlayerChangeSongEvent;
import fr.flagadajones.mediarenderer.events.PlayerClearEvent;
import fr.flagadajones.mediarenderer.events.PlayerErrorEvent;
import fr.flagadajones.mediarenderer.events.PlayerInitializeEvent;
import fr.flagadajones.mediarenderer.events.PlayerInitializeStartEvent;
import fr.flagadajones.mediarenderer.events.PlayerInitializeSuccess;
import fr.flagadajones.mediarenderer.events.PlayerNextEvent;
import fr.flagadajones.mediarenderer.events.PlayerPauseEvent;
import fr.flagadajones.mediarenderer.events.PlayerPlayListEvent;
import fr.flagadajones.mediarenderer.events.PlayerPrevEvent;
import fr.flagadajones.mediarenderer.events.PlayerSeekEvent;
import fr.flagadajones.mediarenderer.events.PlayerSetVolumeEvent;
import fr.flagadajones.mediarenderer.events.PlayerSongUpdateEvent;
import fr.flagadajones.mediarenderer.events.PlayerStartEvent;
import fr.flagadajones.mediarenderer.events.PlayerStopEvent;
import fr.flagadajones.mediarenderer.player.StatefulMediaPlayer;
import fr.flagadajones.mediarenderer.util.BusManager;

public class MediaPlayerService extends Service implements OnBufferingUpdateListener, OnInfoListener,
        OnPreparedListener, OnErrorListener, OnCompletionListener {
    private AudioItem mAudioItem;
    public StatefulMediaPlayer mMediaPlayer = new StatefulMediaPlayer();
    public List<fr.flagadajones.mediarenderer.AudioItem> playlist = new ArrayList<fr.flagadajones.mediarenderer.AudioItem>();
    public int trackPosition;
    private UpdateThread updateThread = new UpdateThread(this);

    public MediaPlayerService() {
        BusManager.getInstance().register(this);
    }

    @Subscribe
    public void clearPlayList(PlayerClearEvent event) {
        playlist.clear();
        BusManager.getInstance().post(updateMediaInfo());
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        BusManager.getInstance().unregister(this);
    }

    public AudioItem getAudioItem() {
        return mAudioItem;

    }

    public StatefulMediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    private void initializePlayer(AudioItem audioStream) {

        BusManager.getInstance().post(new PlayerInitializeStartEvent("Connecting..."));
        mMediaPlayer.setAudioItem(audioStream);
        trackPosition = playlist.indexOf(audioStream);
        mAudioItem = audioStream;

        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnInfoListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.prepareAsync();
        BusManager.getInstance().post(new PlayerChangeSongEvent(audioStream, playlist));
        BusManager.getInstance().post(updateMediaInfo());

    }

    @Subscribe
    public void initializePlayer(PlayerInitializeEvent event) {
        if (event.pos != -1)
            initializePlayer(playlist.get(event.pos));
        else
            initializePlayer(event.item);
    }

    // public void pause() {
    // mMediaPlayer.pause();
    // BusManager.getInstance().post(new PlayerPauseEvent());
    // }
    @Subscribe
    public void nextSong(PlayerNextEvent event) {
        int index = playlist.indexOf(mAudioItem);
        if (index < playlist.size() - 1) {
            initializePlayer(playlist.get(index + 1));

        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer player, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        int index = playlist.indexOf(mAudioItem);
        if (index < playlist.size() - 1) {
            initializePlayer(playlist.get(index + 1));
        } else {
            mp.reset();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMediaPlayer.release();
    }

    @Override
    public boolean onError(MediaPlayer player, int what, int extra) {
        mMediaPlayer.reset();
        BusManager.getInstance().post(new PlayerErrorEvent());
        return true;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer player) {
        BusManager.getInstance().post(new PlayerInitializeSuccess(player.getDuration()));
        BusManager.getInstance().post(new PlayerStartEvent());
    }

    @Subscribe
    public void onPrevSong(PlayerPrevEvent event) {
        int index = playlist.indexOf(mAudioItem);
        if (index > 0) {
            initializePlayer(playlist.get(index - 1));
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Subscribe
    public void pauseMediaPlayer(PlayerPauseEvent event) {
        Log.d("MediaPlayerService", "pauseMediaPlayer() called");
        mMediaPlayer.pause();
        stopForeground(true);

        updateThread.stop();

    }

    public void resetMediaPlayer() {
        stopForeground(true);
        mMediaPlayer.reset();

    }

    @Subscribe
    public void seekTo(PlayerSeekEvent event) {

        mMediaPlayer.seekTo(event.msec);

    }

    public void setAudioItem(AudioItem audioItem) {
        this.mAudioItem = audioItem;
        mMediaPlayer.setAudioItem(audioItem);
    }

    @Subscribe
    public void setPlayList(PlayerPlayListEvent event) {
        playlist = event.list;
    }

    @Subscribe
    public void setVolume(PlayerSetVolumeEvent event) {
        mMediaPlayer.setVolume(event.volume, event.volume);
    }

    @Subscribe
    public void startMediaPlayer(PlayerStartEvent event) {
        if (mMediaPlayer.isStopped() || mMediaPlayer.isEmpty()) {
            mMediaPlayer.prepareAsync();
            return;
        }
        Context context = getApplicationContext();

        Notification notification = new Notification();
        notification.icon = android.R.drawable.ic_media_play;
        notification.tickerText = "FJ MediaPlayer";
        notification.when = System.currentTimeMillis();

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        CharSequence contentTitle = "MediaPlayerService Is Playing";
        CharSequence contentText = mAudioItem.title;
        notification.setLatestEventInfo(context, contentTitle, contentText, pendingIntent);
        startForeground(1, notification);

        Log.d("MediaPlayerService", "startMediaPlayer() called");
        try {
            mMediaPlayer.start();
        } catch (Exception e) {
            Log.e("MediaPlayerService", e.toString());
        }
        updateThread.start();
    }

    @Subscribe
    public void stopMediaPlayer(PlayerStopEvent event) {
        stopForeground(true);
        mMediaPlayer.stop();
        mMediaPlayer.seekTo(0);

        updateThread.stop();
    }

    @Produce
    public PlayerSongUpdateEvent updateMediaInfo() {
        PlayerSongUpdateEvent event = new PlayerSongUpdateEvent();
        event.playListSize = playlist.size();
        event.trackPosition = trackPosition;
        if (mAudioItem != null) {
            event.trackUrl = mAudioItem.url;
            event.trackDuration = mAudioItem.duration;

            // FIXME : corriger avec les vraies valeurs
            event.mediaDuration = "00:00;00";
            event.mediaUrl = mAudioItem.url;
        }
        return event;
    }
}
