package fr.fladajonesjones.MediaControler.activity;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.simonvt.menudrawer.MenuDrawer;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import fr.fladajonesjones.MediaControler.Application;
import fr.fladajonesjones.MediaControler.R;
import fr.fladajonesjones.MediaControler.events.UpnpServerLoadingEvent;
import fr.fladajonesjones.MediaControler.events.UpnpServerLoadingOkEvent;
import fr.fladajonesjones.MediaControler.manager.UpnpDeviceManager;
import fr.fladajonesjones.MediaControler.menu.MenuDrawerUtil;
import fr.flagadajones.android.ViewServer;
import fr.flagadajones.media.util.BusManager;

public class DashBoardActivity extends FragmentActivity {


    private Menu menu;

    @Subscribe
    public void onLoading(UpnpServerLoadingEvent event) {
        showProgressDialog();
    }

    @Subscribe
    public void onLoadingOk(UpnpServerLoadingOkEvent event) {
        removeProgressDialog();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Application.activity = this;
        // Activate StrictMode
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
            .detectAll().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll()
            .penaltyLog().build());
        
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(true); 
               
        
        MenuDrawerUtil.initMenuDrawerManager(this, R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

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
        BusManager.getInstance().register(this);
        ViewServer.get(this).setFocusedWindow(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        BusManager.getInstance().register(this);
    }

    @Override
    protected void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);
        // MenuDrawerUtil.onRestoreDrawerState();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // outState.putParcelable(STATE_MENUDRAWER, mMenuDrawer.onSaveDrawerState());
        // outState.putInt(STATE_ACTIVE_POSITION, MenuDrawerUtil.mActivePosition);
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbarmenu, menu);
        this.menu=menu;
        return true;
   
    }

    public void showToast(final String msg, final boolean longLength) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), msg, longLength ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    public void showProgressDialog() {
        //pd = ProgressDialog.show(this, "Chargement", "", true, false);
        MenuItem menuItem=menu.getItem(R.id.action_refresh);
        menuItem.setActionView(R.layout.progressbar);
        menuItem.expandActionView();
        
    }

    public void removeProgressDialog() {
        //pd.dismiss();
        MenuItem menuItem=menu.getItem(R.id.action_refresh);
        menuItem.collapseActionView();
        menuItem.setActionView(null);
    }

}
