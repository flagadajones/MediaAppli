package fr.fladajonesjones.MediaControler.menu;

import java.util.ArrayList;
import java.util.List;

import net.simonvt.menudrawer.MenuDrawer;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import fr.fladajonesjones.MediaControler.Application;
import fr.fladajonesjones.MediaControler.R;
import fr.fladajonesjones.MediaControler.activity.AlbumFragment;
import fr.fladajonesjones.MediaControler.activity.DeviceFragment;
import fr.fladajonesjones.MediaControler.activity.NowPlayingFragment;
import fr.fladajonesjones.MediaControler.activity.RadioGridViewFragment;
import fr.fladajonesjones.MediaControler.activity.RendererGridFragment;
import fr.fladajonesjones.MediaControler.upnp.UpnpRendererDevice;

public class MenuDrawerUtil {
	static{
		 BroadcastReceiver mStatusListener = new BroadcastReceiver() {
		        @Override
		        public void onReceive(Context context, Intent intent) {
		          
		        	mAdapter.notifyDataSetChanged();
		        }
		    };
		IntentFilter f = new IntentFilter();
        f.addAction(UpnpRendererDevice.STATUT_CHANGED);
		 Application.instance.registerReceiver(mStatusListener, new IntentFilter(f));

	}
    public static MenuDrawer mMenuDrawer;

    public static MenuAdapter mAdapter;
    private static MenuListView mList;
    private static List<Object> items = null;

    private static List<Object> configs = null;
    public static int mActivePosition = -1;
    public static FragmentActivity mActivity;


    // private static Parcelable parc = null;

    // public static void onRestoreDrawerState() {
    // if (parc != null)
    // mMenuDrawer.onRestoreDrawerState(parc);
    // }

   
    
    private static void initAdapter() {
        mAdapter.clear();
        mAdapter.addAll(items);
        List<Object> l=new ArrayList<Object>();
        l.add(new Category("En Cours"));
        mAdapter.addAll(l);
        mAdapter.addAll(configs);
        mAdapter.notifyDataSetChanged();
    }

    public static void toggleMenu() {
        mMenuDrawer.toggleMenu();
    }

    public static void closeMenu() {
        mMenuDrawer.closeMenu();
    }

    public static int getDrawerState() {
        return mMenuDrawer.getDrawerState();
    }

    public static void initMenuDrawerManager(FragmentActivity activity, int layout) {
        mActivity = activity;
        // if (mMenuDrawer != null)
        // parc = mMenuDrawer.onSaveDrawerState();

        mMenuDrawer = MenuDrawer.attach(activity, MenuDrawer.MENU_DRAG_WINDOW);
        mMenuDrawer.setContentView(layout);

        // mActivePosition =position;
        if (items == null) {
            items = new ArrayList<Object>();
            items.add(new Item("Musique", R.drawable.ic_action_refresh_dark));
            items.add(new Item("Radio", R.drawable.ic_action_select_all_dark));
            mAdapter = new MenuAdapter(items);
        }

        if (configs == null) {
            configs = new ArrayList<Object>();
            configs.add(new Category("Conf"));
            configs.add(new Item("Renderers", R.drawable.ic_action_refresh_dark));
            configs.add(new Item("Servers", R.drawable.ic_action_refresh_dark));
            mAdapter.addAll(configs);
        }
        // A custom ListView is needed so the drawer can be notified when it's scrolled. This is to update the position
        // of the arrow indicator.
        mList = new MenuListView(activity);

        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(mItemClickListener);
        mList.setOnScrollChangedListener(new MenuListView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                mMenuDrawer.invalidate();
            }
        });

        mMenuDrawer.setMenuView(mList);
    }

    private static AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mActivePosition = position;
            mMenuDrawer.setActiveView(view, position);

            Object item = mAdapter.getItem(position);
            if (item instanceof Item && ((Item) item).mTitle.equals("Musique")) {
                replaceFragment(new AlbumFragment());
            } else if (item instanceof Item && ((Item) item).mTitle.equals("Renderers")) {
            	//replaceFragment(new DeviceFragment());
            	replaceFragment(new RendererGridFragment());
            } else if (item instanceof Item && ((Item) item).mTitle.equals("Servers")) {
            	replaceFragment(new DeviceFragment());
            	//replaceFragment(new DashBoardFragment());
            } else if (item instanceof Item && ((Item) item).mTitle.equals("Radio")) {
                replaceFragment(new RadioGridViewFragment());
            } else if (item instanceof UpnpRendererDevice) {
                replaceFragment(new NowPlayingFragment((UpnpRendererDevice) item));
            }

        }

        private void replaceFragment(Fragment fragment) {
            FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.details, fragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        }
    };

}
