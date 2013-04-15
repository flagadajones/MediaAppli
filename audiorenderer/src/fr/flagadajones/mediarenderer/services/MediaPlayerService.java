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

import fr.fladajonesjones.media.model.Piste;
import fr.flagadajones.media.util.BusManager;
import fr.flagadajones.media.util.UpnpTransformer;
import fr.flagadajones.mediarenderer.activity.MainActivity;
import fr.flagadajones.mediarenderer.events.PlayerChangeSongEvent;
import fr.flagadajones.mediarenderer.events.PlayerClearEvent;
import fr.flagadajones.mediarenderer.events.PlayerErrorEvent;
import fr.flagadajones.mediarenderer.events.PlayerInitializeEvent;
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

public class MediaPlayerService extends Service implements OnBufferingUpdateListener, OnInfoListener,
        OnPreparedListener, OnErrorListener, OnCompletionListener {
    protected StatefulMediaPlayer mMediaPlayer = new StatefulMediaPlayer();
    private UpdateThread updateThread = new UpdateThread(this);
    private List<Piste> playlist = new ArrayList<Piste>();
    private String mediaDuration="00:00:00";
    private int trackPosition;

    public MediaPlayerService() {
        BusManager.getInstance().register(this);

        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnInfoListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);

    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        BusManager.getInstance().unregister(this);
    }

    @Produce
    public PlayerSongUpdateEvent updateMediaInfo() {
        PlayerSongUpdateEvent event = new PlayerSongUpdateEvent();
        event.playListSize = playlist.size();
        if (event.playListSize != 0) {
            event.trackPosition = trackPosition;

            Piste piste = playlist.get(trackPosition);
            event.trackUrl = piste.url;
            event.trackDuration = piste.duree;
            event.mediaDuration = mediaDuration;
            event.mediaUrl = piste.url;
        }
        return event;
    }

    @Subscribe
    public void setVolume(PlayerSetVolumeEvent event) {
        mMediaPlayer.setVolume(event.volume, event.volume);
    }

    @Subscribe
    public void onInitializePlayer(PlayerInitializeEvent event) {
        if (event.pos != -1) {
            initializePlayer();
            trackPosition = event.pos;
        } else {
            trackPosition = 0;
            playlist.clear();
            playlist.add(event.item);
            mediaDuration=event.item.duree;
            initializePlayer();
        }
    }

    private void initializePlayer() {

        Piste piste = playlist.get(trackPosition);
        mMediaPlayer.setAudioItem(piste);

        // mMediaPlayer.prepareAsync();
        BusManager.getInstance().post(new PlayerChangeSongEvent(piste, playlist));
        BusManager.getInstance().post(updateMediaInfo());

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
        if (trackPosition < playlist.size() - 1) {
            trackPosition = trackPosition + 1;
            initializePlayer();
        } else {
            mMediaPlayer.reset();
        }
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Subscribe
    public void pauseMediaPlayer(PlayerPauseEvent event) {
        if (mMediaPlayer.isStarted()) {
            Log.d("MediaPlayerService", "pauseMediaPlayer() called");

            mMediaPlayer.pause();
            stopForeground(true);

            if (UpdateThread.isRunning())
                updateThread.stop();

        }
    }

    @Subscribe
    public void startMediaPlayer(PlayerStartEvent event) {
        if (mMediaPlayer.isStopped() || mMediaPlayer.isInitialized()) {
            mMediaPlayer.prepareAsync();
            return;
        }

        if (mMediaPlayer.isPrepared() || mMediaPlayer.isPaused() || mMediaPlayer.isPlaybackCompleted()) {
            initNotification();

            Log.d("MediaPlayerService", "startMediaPlayer() called");
            try {
                mMediaPlayer.start();
                if (!UpdateThread.isRunning())
                    updateThread.start();

            } catch (Exception e) {
                Log.e("MediaPlayerService", e.toString());
            }
        } else {
            Log.e("MediaPlayerService : Start Impossible sur Etat :", mMediaPlayer.getState().toString());
        }

    }

    private void initNotification() {
        Context context = getApplicationContext();

        Notification notification = new Notification();
        notification.icon = android.R.drawable.ic_media_play;
        notification.tickerText = "FJ MediaPlayer";
        notification.when = System.currentTimeMillis();

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        CharSequence contentTitle = "MediaPlayerService Is Playing";
        CharSequence contentText = playlist.get(trackPosition).titre;
        notification.setLatestEventInfo(context, contentTitle, contentText, pendingIntent);
        startForeground(1, notification);

    }

    @Subscribe
    public void stopMediaPlayer(PlayerStopEvent event) {
        if (mMediaPlayer.isPaused() || mMediaPlayer.isPlaybackCompleted() || mMediaPlayer.isStarted()
                || mMediaPlayer.isStopped() || mMediaPlayer.isPrepared()) {
            stopForeground(true);
            mMediaPlayer.stop();
            mMediaPlayer.seekTo(0);

            if (UpdateThread.isRunning())
                updateThread.stop();
            BusManager.getInstance().post(updateMediaInfo());
        } else {
            Log.e("MediaPlayerService : Stop Impossible sur Etat :", mMediaPlayer.getState().toString());
        }
    }

    @Subscribe
    public void nextSong(PlayerNextEvent event) {
        if (trackPosition < playlist.size() - 1) {
            trackPosition = trackPosition + 1;
            initializePlayer();
        }
    }

    @Subscribe
    public void onPrevSong(PlayerPrevEvent event) {
        if (trackPosition > 0) {
            trackPosition = trackPosition - 1;
            initializePlayer();
        }
    }

    @Subscribe
    public void seekTo(PlayerSeekEvent event) {
        mMediaPlayer.seekTo(event.msec);
        BusManager.getInstance().post(updateMediaInfo());

    }

    @Subscribe
    public void clearPlayList(PlayerClearEvent event) {
        playlist.clear();
        mMediaPlayer.reset();
        BusManager.getInstance().post(updateMediaInfo());

    }

    @Subscribe
    public void onPlayList(PlayerPlayListEvent event) {
        playlist = event.list;
        mediaDuration=UpnpTransformer.calculTotalTime(playlist);
        BusManager.getInstance().post(updateMediaInfo());
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
}
