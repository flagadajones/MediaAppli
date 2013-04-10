package fr.fladajonesjones.MediaControler.activity;


import java.util.logging.Level;
import java.util.logging.Logger;

import net.simonvt.menudrawer.MenuDrawer;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import fr.fladajonesjones.MediaControler.Application;
import fr.fladajonesjones.MediaControler.R;
import fr.fladajonesjones.MediaControler.ViewServer;
import fr.fladajonesjones.MediaControler.manager.UpnpDeviceManager;
import fr.fladajonesjones.MediaControler.menu.MenuDrawerUtil;
import fr.fladajonesjones.MediaControler.upnp.UpnpServerDevice;

public class DashBoardActivity extends FragmentActivity {

    /*
     * private static final String STATE_MENUDRAWER = "net.simonvt.menudrawer.samples.ContentSample.menuDrawer"; private
     * static final String STATE_ACTIVE_POSITION = "net.simonvt.menudrawer.samples.ContentSample.activePosition";
     * private static final String STATE_CONTENT_TEXT = "net.simonvt.menudrawer.samples.ContentSample.contentText";
     */

    private ProgressDialog pd;
    
    BroadcastReceiver mStatusListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	
            if (intent.getAction().equals(UpnpServerDevice.LOADING)) {
               showProgressDialog();
            }
            if (intent.getAction().equals(UpnpServerDevice.LOADING_OK)) {
                removeProgressDialog();
            }
        }
    };
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Application.activity=this;
        MenuDrawerUtil.initMenuDrawerManager(this, R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        IntentFilter f = new IntentFilter();
        f.addAction(UpnpServerDevice.LOADING);
        f.addAction(UpnpServerDevice.LOADING_OK);
        Application.instance.registerReceiver(mStatusListener, new IntentFilter(f));
        
        ViewServer.get(this).addWindow(this);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.details, new RendererGridFragment());
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	ViewServer.get(this).removeWindow(this);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	ViewServer.get(this).setFocusedWindow(this);
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);
       // MenuDrawerUtil.onRestoreDrawerState();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    //     outState.putParcelable(STATE_MENUDRAWER, mMenuDrawer.onSaveDrawerState());
    //     outState.putInt(STATE_ACTIVE_POSITION, MenuDrawerUtil.mActivePosition);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            MenuDrawerUtil.toggleMenu();
            return true;

        case 0:
            searchNetwork();
            break;
        case 2:
            Logger logger = Logger.getLogger("org.teleal.cling");
            if (logger.getLevel().equals(Level.FINEST)) {
                Toast.makeText(this, "R.string.disabling_debug_logging", Toast.LENGTH_SHORT).show();
                logger.setLevel(Level.INFO);
            } else {
                Toast.makeText(this, "R.string.enabling_debug_logging", Toast.LENGTH_SHORT).show();
                logger.setLevel(Level.FINEST);
            }
            break;
        // case 1:
        // if (upnpService != null) {
        // SwitchableRouter router = (SwitchableRouter) upnpService.get().getRouter();
        // if (router.isEnabled()) {
        // Toast.makeText(this, R.string.disabling_router, Toast.LENGTH_SHORT).show();
        // router.disable();
        // } else {
        // Toast.makeText(this, R.string.enabling_router, Toast.LENGTH_SHORT).show();
        // router.enable();
        // }
        // }
        // break;

        }
        return super.onOptionsItemSelected(item);
    }
    
    protected void searchNetwork() {
        Toast.makeText(this, "Recherche", Toast.LENGTH_SHORT).show();
        UpnpDeviceManager.getInstance().search();
        
    }
    
    @Override
    public void onBackPressed() {
        final int drawerState = MenuDrawerUtil.getDrawerState();
        if (drawerState == MenuDrawer.STATE_OPEN || drawerState == MenuDrawer.STATE_OPENING) {
            MenuDrawerUtil.closeMenu();
            return;
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 0, 0, "Search").setIcon(android.R.drawable.ic_menu_search);
        menu.add(0, 2, 0, "Debug").setIcon(android.R.drawable.ic_menu_info_details);
        return true;
    }

    
    public void showToast(final String msg, final boolean longLength) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), msg, longLength ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
            }
        });
    }
  
    public void showProgressDialog(){
    pd = ProgressDialog.show(this, "Chargement", "", true,
            false);
  
    }
    public void removeProgressDialog(){
        pd.dismiss();    
    }

}
