package fr.fladajonesjones.MediaControler;

import fr.fladajonesjones.MediaControler.activity.DashBoardActivity;
import fr.fladajonesjones.MediaControler.database.MySQLOpenHelper;
import fr.fladajonesjones.MediaControler.loader.ImageLoader;
import fr.fladajonesjones.MediaControler.manager.UpnpDeviceManager;

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
    

    
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        imageLoader = new ImageLoader(getApplicationContext());

        MySQLOpenHelper.getInstance(getApplicationContext());

        UpnpDeviceManager.getInstance();
      

    }

  
 
}
