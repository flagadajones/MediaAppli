package fr.flagadajones.mediarenderer;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import fr.flagadajones.android.loader.ImageLoader;
import fr.flagadajones.mediarenderer.services.MediaPlayerService;
import fr.flagadajones.mediarenderer.upnp.MediaRenderer;
import fr.flagadajones.mediarenderer.upnp.service.MyRendererUpnpService;
import org.fourthline.cling.android.AndroidUpnpService;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Application extends android.app.Application {
    final private static Logger log = Logger.getLogger(Application.class.getName());
    static public ImageLoader imageLoader = null;
    public static Application instance = null;

    public static MediaRenderer mediaRenderer;
    public static AndroidUpnpService upnpService;

    // ###############################################################################
    // UPNP Service
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

    public void bindToUpnpService() {
        Intent intent = new Intent(this, MyRendererUpnpService.class);

        if (upnpServiceRunning()) {
            // Bind to LocalService
            bindService(intent, upnpServiceConnection, Context.BIND_AUTO_CREATE);
        } else {
            startService(intent);
            bindService(intent, upnpServiceConnection, Context.BIND_AUTO_CREATE);
        }

    }

    private boolean upnpServiceRunning() {

        ActivityManager manager = (ActivityManager) Application.instance.getSystemService(ACTIVITY_SERVICE);

        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("fr.flagadajones.mediarenderer.upnp.service.MyRendererUpnpService".equals(service.service
                    .getClassName())) {
                return true;
            }
        }

        return false;
    }

    // ###############################################################################
    // MediaPlayerService
    // ###############################################################################

    private boolean mBound;

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mediaPlayerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder serviceBinder) {
            Log.d("Application", "service connected");
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    public void bindToMediaPlayerService() {
        Intent intent = new Intent(this, MediaPlayerService.class);

        if (mediaPlayerServiceRunning()) {
            // Bind to LocalService
            bindService(intent, mediaPlayerServiceConnection, Context.BIND_AUTO_CREATE);
        } else {
            startService(intent);
            bindService(intent, mediaPlayerServiceConnection, Context.BIND_AUTO_CREATE);
        }

    }

    private boolean mediaPlayerServiceRunning() {

        ActivityManager manager = (ActivityManager) Application.instance.getSystemService(ACTIVITY_SERVICE);

        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("fr.flagadajones.mediarenderer.services.MediaPlayerService".equals(service.service.getClassName())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        // org.teleal.common.logging.LoggingUtil
        // .resetRootHandler(new FixedAndroidHandler());

        imageLoader = new ImageLoader(getApplicationContext(), R.drawable.stub);

        java.util.logging.Logger.getLogger("org.teleal.cling").setLevel(Level.OFF);

        bindToUpnpService();

        bindToMediaPlayerService();

    }

    public void onTerminate() {
        super.onTerminate();
        getApplicationContext().unbindService(upnpServiceConnection);
        getApplicationContext().unbindService(mediaPlayerServiceConnection);
    }

}
