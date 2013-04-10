package fr.flagadajones.mediarenderer.services;

import java.util.Timer;
import java.util.TimerTask;

import fr.flagadajones.mediarenderer.events.PlayerStartEvent;
import fr.flagadajones.mediarenderer.events.PlayerUpdatePosEvent;
import fr.flagadajones.mediarenderer.util.BusManager;

import android.util.Log;

public class UpdateThread {
    // private int counter = 0, incrementby = 1;
    private static boolean isRunning = false;
    private MediaPlayerService mediaPlayerService;

    public static boolean isRunning() {
        return isRunning;
    }

    // private NotificationManager nm;
    private Timer timer = new Timer();

    public UpdateThread(MediaPlayerService mediaPlayerService) {
        this.mediaPlayerService = mediaPlayerService;

    }

    public void start() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                onTimerTick();
            }
        }, 0, 2000L);
        isRunning = true;
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        Log.i("UpdateService", "Service Stopped.");
        isRunning = false;

    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        stop();
    }

    private void onTimerTick() {
       // Log.d("TimerTick", "Timer doing work.");
        try {

            sendMessageToUI();

        } catch (Throwable t) { // you should always ultimately catch all exceptions in timer tasks.
            Log.e("TimerTick", "Timer Tick Failed.", t);
        }
    }

    private void sendMessageToUI() {
        if (mediaPlayerService.mMediaPlayer != null && mediaPlayerService.mMediaPlayer.isPlaying())
            BusManager.getInstance().post(
                    new PlayerUpdatePosEvent(mediaPlayerService.mMediaPlayer.getCurrentPosition()));
    }

}
