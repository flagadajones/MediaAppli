package fr.flagadajones.mediarenderer;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.fourthline.cling.android.AndroidUpnpService;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import fr.flagadajones.mediarenderer.loader.ImageLoader;
import fr.flagadajones.mediarenderer.services.MediaPlayerService;
import fr.flagadajones.mediarenderer.upnp.MediaRenderer;
import fr.flagadajones.mediarenderer.upnp.service.MyAVTransportService;
import fr.flagadajones.mediarenderer.upnp.service.MyRendererUpnpService;
import fr.flagadajones.mediarenderer.util.FixedAndroidHandler;

public class Application extends android.app.Application {
	final private static Logger log = Logger.getLogger(Application.class
			.getName());
	static public ImageLoader imageLoader = null;
	public static Application instance = null;
	public static AudioItem item;

	public static MediaRenderer mediaRenderer;
	public static AndroidUpnpService upnpService;
	// ###############################################################################
	// UPNP
	// ###############################################################################
	private ServiceConnection upnpServiceConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder service) {
			upnpService = (AndroidUpnpService) service;
			// Add the bound local device to the registry
			try {
				mediaRenderer = new MediaRenderer();

				upnpService.getRegistry().addDevice(mediaRenderer.getDevice());
				mediaRenderer.runLastChangePushThread();

			} catch (Exception e) {
				log.log(Level.SEVERE, e.getMessage(), e);
			}

		}

		public void onServiceDisconnected(ComponentName className) {
			upnpService = null;
		}
	};

	// ###############################################################################
	// MediaPlayerService
	// ###############################################################################

	private boolean mBound;
	// public MediaPlayerService mService;
	// public StatefulMediaPlayer mMediaPlayer;

	/**
	 * Defines callbacks for service binding, passed to bindService()
	 */
	private ServiceConnection mediaPlayerServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className,
				IBinder serviceBinder) {
			Log.d("Application", "service connected");

			// bound with Service. get Service instance
			// MediaPlayerBinder binder = (MediaPlayerBinder) serviceBinder;
			// mService = binder.getService();
			// mediaRenderer.setMediaPlayerService(mService);
			// send this instance to the service, so it can make callbacks on
			// this instance as a client
			// mService.setClient(Application.this);
			// mMediaPlayer=mService.getMediaPlayer();
			mBound = true;

			// Set play/pause button to reflect state of the service's contained
			// player
			// final ToggleButton playPauseButton = (ToggleButton)
			// findViewById(R.id.playPauseButton);
			// playPauseButton.setChecked(mService.getMediaPlayer().isPlaying());

			// Set station Picker to show currently set stream station
			// Spinner stationPicker = (Spinner)
			// findViewById(R.id.stationPicker);
			// if(mService.getMediaPlayer() != null &&
			// mService.getMediaPlayer().getStreamStation() != null) {
			// for (int i = 0; i < CONSTANTS.STATIONS.length; i++) {
			// if
			// (mService.getMediaPlayer().getStreamStation().equals(CONSTANTS.STATIONS[i]))
			// {
			// stationPicker.setSelection(i);
			// mSelectedStream = (StreamStation)
			// stationPicker.getItemAtPosition(i);
			// }
			//
			// }
			// }

		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
		}
	};

	/**
	 * Binds to the instance of MediaPlayerService. If no instance of
	 * MediaPlayerService exists, it first starts a new instance of the service.
	 */
	public void bindToService() {
		Intent intent = new Intent(this, MediaPlayerService.class);

		if (mediaPlayerServiceRunning()) {
			// Bind to LocalService
			bindService(intent, mediaPlayerServiceConnection,
					Context.BIND_AUTO_CREATE);
		} else {
			startService(intent);
			bindService(intent, mediaPlayerServiceConnection,
					Context.BIND_AUTO_CREATE);
		}

	}

	/**
	 * Determines if the MediaPlayerService is already running.
	 * 
	 * @return true if the service is running, false otherwise.
	 */
	private boolean mediaPlayerServiceRunning() {

		ActivityManager manager = (ActivityManager) Application.instance
				.getSystemService(ACTIVITY_SERVICE);

		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if ("fr.flagadajones.mediarenderer.services.MediaPlayerService"
					.equals(service.service.getClassName())) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		instance = this;
//		org.teleal.common.logging.LoggingUtil
//				.resetRootHandler(new FixedAndroidHandler());

		imageLoader = new ImageLoader(getApplicationContext());

		java.util.logging.Logger.getLogger("org.teleal.cling").setLevel(
				Level.OFF);

		bindService(new Intent(this, MyRendererUpnpService.class),
				upnpServiceConnection, Context.BIND_AUTO_CREATE);

		bindToService();

	}

	public void onTerminate() {
		super.onTerminate();
		if (upnpService != null) {
			// upnpService.getRegistry().removeListener(registryListener);

		}
		getApplicationContext().unbindService(upnpServiceConnection);
	}

}
