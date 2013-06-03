package fr.fladajonesjones.MediaControler.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.squareup.otto.Subscribe;

import fr.fladajonesjones.MediaControler.R;
import fr.fladajonesjones.MediaControler.adapter.ServerGridAdapter;
import fr.fladajonesjones.MediaControler.events.UpnpServerAddEvent;
import fr.fladajonesjones.MediaControler.events.UpnpServerRemoveEvent;
import fr.fladajonesjones.MediaControler.manager.UpnpDeviceManager;
import fr.fladajonesjones.MediaControler.menu.MenuDrawerUtil;
import fr.flagadajones.media.util.BusManager;

public class ServerGridFragment extends Fragment {

    @Subscribe
    public void onServerDeviceAdded(final  UpnpServerAddEvent event) {
         getActivity().runOnUiThread(new Runnable() {
         public void run() {
        if (serverListAdapter.getPosition(event.device) == -1)
            serverListAdapter.add(event.device);
        else
            serverListAdapter.notifyDataSetChanged();
         }});
    }

    @Subscribe
    public void onServerDeviceRemoved(final UpnpServerRemoveEvent event) {
         getActivity().runOnUiThread(new Runnable() {
         public void run() {
        serverListAdapter.remove(event.device);
         }});
    }

    // private static final Logger log =
    // Logger.getLogger(BrowseActivity.class.getName());

    private ServerGridAdapter serverListAdapter;

    @Override
    public void onResume() {
        super.onResume();
        BusManager.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusManager.getInstance().unregister(this);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MenuDrawerUtil.toggleMenu();
    };

    private void initServerList(LayoutInflater inflater, View layout) {
        
        GridView serverListView = (GridView) layout.findViewById(R.id.server_gridview);
        serverListAdapter = new ServerGridAdapter(getActivity());

        if (UpnpDeviceManager.getInstance().lstServer.isEmpty()) {
            if (UpnpDeviceManager.getInstance().libraryDevice != null)
                serverListAdapter.add(UpnpDeviceManager.getInstance().libraryDevice);
        } else
            serverListAdapter.addAll(UpnpDeviceManager.getInstance().lstServer);
        serverListView.setAdapter(serverListAdapter);

      
    }

    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View layout = inflater.inflate(R.layout.fragment_server_grid_view, null);

        initServerList(inflater, layout);

        return layout;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
