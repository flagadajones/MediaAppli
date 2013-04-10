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

/**
 * An extension of android.app.Service class which provides access to a
 * StatefulMediaPlayer.<br>
 * 
 * @author rootlicker http://speakingcode.com
 * @see com.speakingcode.android.media.mediaplayer.StatefulMediaPlayer
 */



public class MediaPlayerService extends Service implements
		OnBufferingUpdateListener, OnInfoListener, OnPreparedListener,
		OnErrorListener, OnCompletionListener {
	public StatefulMediaPlayer mMediaPlayer = new StatefulMediaPlayer();
	private UpdateThread updateThread = new UpdateThread(this);
	private AudioItem mAudioItem;
	public List<fr.flagadajones.mediarenderer.AudioItem> playlist = new ArrayList<fr.flagadajones.mediarenderer.AudioItem>();
	public int trackPosition;

	public MediaPlayerService() {
		BusManager.getInstance().register(this);
	}

	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		BusManager.getInstance().unregister(this);
	}

	public AudioItem getAudioItem() {
		return mAudioItem;

	}

	@Produce public PlayerSongUpdateEvent  updateMediaInfo(){
		PlayerSongUpdateEvent event = new PlayerSongUpdateEvent();
		event.playListSize=playlist.size();
		event.trackPosition=trackPosition;
		if(mAudioItem!=null){
		event.trackUrl=mAudioItem.url;
		event.trackDuration=mAudioItem.duration;
		
		//FIXME : corriger avec les vraies valeurs
		event.mediaDuration="00:00;00";
		event.mediaUrl=mAudioItem.url;
		}
		return event;
	}
	
	/**
	 * Sets a StatefulMediaPlayer's data source as the provided StreamStation
	 * 
	 * @param audioItem
	 *            the StreamStation to set as the data source
	 */
	public void setAudioItem(AudioItem audioItem) {
		this.mAudioItem = audioItem;
		mMediaPlayer.setAudioItem(audioItem);
	}

	@Subscribe
	public void setVolume(PlayerSetVolumeEvent event) {
		mMediaPlayer.setVolume(event.volume, event.volume);
	}

	/**
	 * Returns the contained StatefulMediaPlayer
	 * 
	 * @return
	 */
	public StatefulMediaPlayer getMediaPlayer() {
		return mMediaPlayer;
	}

	@Subscribe
	public void initializePlayer(PlayerInitializeEvent event) {
		if (event.pos != -1)
			initializePlayer(playlist.get(event.pos));
		else
			initializePlayer(event.item);
	}

	/**
	 * Initializes a StatefulMediaPlayer for streaming playback of the provided
	 * StreamStation
	 * 
	 * @param audioStream
	 *            The StreamStation representing the station to play
	 */
	public void initializePlayer(AudioItem audioStream) {

		BusManager.getInstance().post(
				new PlayerInitializeStartEvent("Connecting..."));
		// mMediaPlayer = new StatefulMediaPlayer(audioStream);
		mMediaPlayer.setAudioItem(audioStream);
		trackPosition = playlist.indexOf(audioStream);
		mAudioItem = audioStream;
		// try {
		// mMediaPlayer.setDataSource(audioStream.url);
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		mMediaPlayer.setOnBufferingUpdateListener(this);
		mMediaPlayer.setOnInfoListener(this);
		mMediaPlayer.setOnPreparedListener(this);
		mMediaPlayer.setOnCompletionListener(this);
		mMediaPlayer.prepareAsync();
		BusManager.getInstance().post(
				new PlayerChangeSongEvent(audioStream, playlist));
		BusManager.getInstance().post(updateMediaInfo());

	}

	/**
	 * Initializes a StatefulMediaPlayer for streaming playback of the provided
	 * stream url
	 * 
	 * @param streamUrl
	 *            The URL of the stream to play.
	 */
	// public void initializePlayer(String streamUrl) {
	//
	// mMediaPlayer = new StatefulMediaPlayer();
	// mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
	// try {
	// mMediaPlayer.setDataSource(streamUrl);
	// } catch (Exception e) {
	// Log.e("MediaPlayerService", "error setting data source");
	// mMediaPlayer.setState(MPStates.ERROR);
	// }
	// mMediaPlayer.setOnBufferingUpdateListener(this);
	// mMediaPlayer.setOnInfoListener(this);
	// mMediaPlayer.setOnPreparedListener(this);
	// mMediaPlayer.prepareAsync();
	//
	// }

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
		BusManager.getInstance().post(
				new PlayerInitializeSuccess(player.getDuration()));
		BusManager.getInstance().post(new PlayerStartEvent());
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	/**
	 * Pauses the contained StatefulMediaPlayer
	 */
	@Subscribe
	public void pauseMediaPlayer(PlayerPauseEvent event) {
		Log.d("MediaPlayerService", "pauseMediaPlayer() called");
		mMediaPlayer.pause();
		stopForeground(true);

		updateThread.stop();

	}

	/**
	 * Starts the contained StatefulMediaPlayer and foregrounds the service to
	 * support persisted background playback.
	 */

	@Subscribe
	public void startMediaPlayer(PlayerStartEvent event) {
		if (mMediaPlayer.isStopped() || mMediaPlayer.isEmpty()) {
			mMediaPlayer.prepareAsync();
			return;
		}
		Context context = getApplicationContext();

		// set to foreground
		Notification notification = new Notification();
		// android.R.drawable.ic_media_play, "MediaPlayerService",
		// System.currentTimeMillis());
		notification.icon = android.R.drawable.ic_media_play;
		notification.tickerText = "FJ MediaPlayer";
		notification.when = System.currentTimeMillis();

		Intent notificationIntent = new Intent(this, MainActivity.class);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);

		CharSequence contentTitle = "MediaPlayerService Is Playing";
		CharSequence contentText = mAudioItem.title;
		notification.setLatestEventInfo(context, contentTitle, contentText,
				pendingIntent);
		startForeground(1, notification);

		// NotificationManager nm = (NotificationManager) ctx
		// .getSystemService(Context.NOTIFICATION_SERVICE);
		//
		// Resources res = ctx.getResources();
		// Notification.Builder builder = new Notification.Builder(ctx);
		//
		// builder.setContentIntent(contentIntent)
		// .setSmallIcon(R.drawable.some_img)
		// .setLargeIcon(BitmapFactory.decodeResource(res,
		// R.drawable.some_big_img))
		// .setTicker(res.getString(R.string.your_ticker))
		// .setWhen(System.currentTimeMillis())
		// .setAutoCancel(true)
		// .setContentTitle(res.getString(R.string.your_notif_title))
		// .setContentText(res.getString(R.string.your_notif_text));
		// Notification n = builder.build();

		Log.d("MediaPlayerService", "startMediaPlayer() called");
		try {
			mMediaPlayer.start();
		} catch (Exception e) {
			Log.e("MediaPlayerService", e.toString());
		}
		updateThread.start();
	}

	/**
	 * Stops the contained StatefulMediaPlayer.
	 */
	@Subscribe
	public void stopMediaPlayer(PlayerStopEvent event) {
		stopForeground(true);
		mMediaPlayer.stop();
		mMediaPlayer.seekTo(0);
		// mMediaPlayer.release();

		updateThread.stop();
	}

	public void resetMediaPlayer() {
		stopForeground(true);
		mMediaPlayer.reset();

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
		// mMediaPlayer.next();
		// mClient.onNextSong();
		// // Check if last song or not
		// if (++currentPosition >= songs.size()) {
		// currentPosition = 0;
		// nm.cancel(NOTIFY_ID);
		// } else {
		// playSong(MusicDroid.MEDIA_PATH + songs.get(currentPosition));
		// }
	}

	@Subscribe
	public void onPrevSong(PlayerPrevEvent event) {
		int index = playlist.indexOf(mAudioItem);
		if (index > 0) {
			initializePlayer(playlist.get(index - 1));
		}
		// mMediaPlayer.prev();
		// mClient.onPrevSong();
		// if (mp.getCurrentPosition() < 3000 && currentPosition >= 1) {
		// playSong(MusicDroid.MEDIA_PATH + songs.get(--currentPosition));
		// } else {
		// playSong(MusicDroid.MEDIA_PATH + songs.get(currentPosition));
		// }
	}

	@Subscribe
	public void seekTo(PlayerSeekEvent event) {

		mMediaPlayer.seekTo(event.msec);

	}

	@Subscribe
	public void clearPlayList(PlayerClearEvent event) {
		playlist.clear();
		BusManager.getInstance().post(updateMediaInfo());
	}

	@Subscribe
	public void setPlayList(PlayerPlayListEvent event) {
		playlist = event.list;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mMediaPlayer.release();
	}
}
