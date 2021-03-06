package fr.fladajonesjones.MediaControler.menu;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import fr.fladajonesjones.MediaControler.R;
import fr.fladajonesjones.MediaControler.activity.*;
import fr.fladajonesjones.MediaControler.upnp.UpnpRendererDevice;
import net.simonvt.menudrawer.MenuDrawer;

import java.util.ArrayList;
import java.util.List;

public class MenuDrawerUtil {
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
//        List<Object> l = new ArrayList<Object>();
//        l.add(new Category("En Cours"));
//        mAdapter.addAll(l);
        mAdapter.addAll(configs);
        mAdapter.notifyDataSetChanged();
    }

    public static void toggleMenu() {
        // mMenuDrawer.toggleMenu();
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
            items.add(new Item("Musique", R.drawable.menu_cd_2));
            items.add(new Item("Radio", R.drawable.menu_radio));
            mAdapter = new MenuAdapter(items);
        }

        if (configs == null) {
            configs = new ArrayList<Object>();
            //configs.add(new Category("Conf"));
            configs.add(new Item("Renderer", R.drawable.menu_player));
            configs.add(new Item("Server", R.drawable.menu_server));
            configs.add(new Item("Conf", R.drawable.bg_img_notfound));
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
            } else if (item instanceof Item && ((Item) item).mTitle.equals("Renderer")) {
                // replaceFragment(new DeviceFragment());
                replaceFragment(new RendererGridFragment());
            } else if (item instanceof Item && ((Item) item).mTitle.equals("Server")) {
                replaceFragment(new ServerGridFragment());
                // replaceFragment(new DashBoardFragment());
            } else if (item instanceof Item && ((Item) item).mTitle.equals("Conf")) {
                Intent intent = new Intent(mActivity, SettingsActivity.class);
                mActivity.startActivity(intent);
                // replaceFragment(new DashBoardFragment());

            } else if (item instanceof Item && ((Item) item).mTitle.equals("Radio")) {
                replaceFragment(new RadioGridViewFragment());
            } else if (item instanceof UpnpRendererDevice) {
                NowPlayingFragment fragment = new NowPlayingFragment();
                fragment.renderer = (UpnpRendererDevice) item;
                replaceFragment(fragment);
            }

            mMenuDrawer.closeMenu();

        }

        private void replaceFragment(Fragment fragment) {
            FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.details, fragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        }
    };

}
