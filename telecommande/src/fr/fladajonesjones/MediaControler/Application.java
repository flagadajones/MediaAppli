package fr.fladajonesjones.MediaControler;

import com.squareup.picasso.Picasso;
import fr.fladajonesjones.MediaControler.activity.DashBoardActivity;
import fr.fladajonesjones.MediaControler.database.MySQLOpenHelper;
import fr.fladajonesjones.MediaControler.manager.UpnpDeviceManager;
import fr.fladajonesjones.MediaControler.upnp.UpnpRendererDevice;
import fr.fladajonesjones.MediaControler.upnp.UpnpServerDevice;
import fr.flagadajones.android.loader.ImageLoader;

public class Application extends android.app.Application {

    static public ImageLoader imageLoader = null;


    //////////////  default
    static public Application instance;

    static int i;


    /////////////////  Application    


    @Override
    public void onTerminate() {
        // TODO Auto-generated method stub
        super.onTerminate();
        MySQLOpenHelper.closeDB();
    }

    static public DashBoardActivity activity;


    public void onStop() {
     /*   HttpResponseCache cache = HttpResponseCache.getInstalled();

        if (cache != null) {
            cache.flush();
        }
        */
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        imageLoader = new ImageLoader(getApplicationContext(), R.drawable.stub);

        MySQLOpenHelper.getInstance(getApplicationContext());

        UpnpDeviceManager.getInstance();

      /*  try {
            File httpCacheDir = new File(getApplicationContext().getCacheDir(), "http");
            long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
            HttpResponseCache.install(httpCacheDir, httpCacheSize);
        }catch (IOException e) {
               // Log.i(TAG, "HTTP response cache installation failed:" + e);
            }
        */
        Picasso.with(this).setDebugging(true);
        //  initMock();

    }


    private void initMock() {

        UpnpServerDevice server = new UpnpServerDevice();
        server.setUdn("server1");
        server.icone = "http://localhost/toto.pnp";

        UpnpDeviceManager.getInstance().lstServer.add(server);
        server = new UpnpServerDevice();
        server.setUdn("server2");
        server.icone = "http://localhost/toto.pnp";

        UpnpDeviceManager.getInstance().lstServer.add(server);
        server = new UpnpServerDevice();
        server.setUdn("server3");
        server.icone = "http://localhost/toto.pnp";

        UpnpDeviceManager.getInstance().lstServer.add(server);

        UpnpRendererDevice renderer = new UpnpRendererDevice();
        renderer.setUdn("renderer1");
        renderer.icone = "http://localhost/toto.pnp";
        renderer.connected = true;
        UpnpDeviceManager.getInstance().lstRenderer.add(renderer);
        renderer = new UpnpRendererDevice();
        renderer.setUdn("renderer2");
        renderer.icone = "http://localhost/toto.pnp";
        UpnpDeviceManager.getInstance().lstRenderer.add(renderer);
        renderer = new UpnpRendererDevice();
        renderer.setUdn("renderer3");
        renderer.icone = "http://localhost/toto.pnp";
        UpnpDeviceManager.getInstance().lstRenderer.add(renderer);

        MySQLOpenHelper.getInstance(getApplicationContext()).initMock();

    }


}
