package fr.flagadajones.mediarenderer.services;

import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;
import fr.flagadajones.media.util.BusManager;
import fr.flagadajones.mediarenderer.events.PlayerUpdatePosEvent;

public class UpdateThread {
    private static boolean isRunning = false;
    private MediaPlayerService mediaPlayerService;

    public static boolean isRunning() {
        return isRunning;
    }

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
        }, 0, 1000L);
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
        try {

            sendMessageToUI();

        } catch (Throwable t) {
            Log.e("TimerTick", "Timer Tick Failed.", t);
        }
    }

    private void sendMessageToUI() {
        if (mediaPlayerService.mMediaPlayer != null && mediaPlayerService.mMediaPlayer.isPlaying())
            BusManager.getInstance().post(
                    new PlayerUpdatePosEvent(mediaPlayerService.mMediaPlayer.getCurrentPosition()));
    }

}
