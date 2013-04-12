package fr.fladajonesjones.MediaControler.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import fr.fladajonesjones.MediaControler.R;
import fr.fladajonesjones.MediaControler.adapter.DeviceAdapter;
import fr.fladajonesjones.MediaControler.adapter.DeviceAdapter.DeviceDisplayHolder;
import fr.fladajonesjones.MediaControler.events.UpnpRendererAddEvent;
import fr.fladajonesjones.MediaControler.events.UpnpRendererRemoveEvent;
import fr.fladajonesjones.MediaControler.events.UpnpServerAddEvent;
import fr.fladajonesjones.MediaControler.events.UpnpServerRemoveEvent;
import fr.fladajonesjones.MediaControler.manager.UpnpDeviceManager;
import fr.fladajonesjones.MediaControler.menu.MenuDrawerUtil;
import fr.fladajonesjones.MediaControler.upnp.UpnpRendererDevice;
import fr.fladajonesjones.MediaControler.upnp.UpnpServerDevice;
import fr.flagadajones.media.util.BusManager;

public class DeviceFragment extends Fragment {

    @Subscribe
    public void onRendererDeviceAdded(UpnpRendererAddEvent event) {
        // getActivity().runOnUiThread(new Runnable() {
        // public void run() {
        if (rendererListAdapter.getPosition(event.device) == -1)
            rendererListAdapter.add(event.device);
        else
            rendererListAdapter.notifyDataSetChanged();
        // }});
    }

    @Subscribe
    public void onRendererDeviceRemoved(UpnpRendererRemoveEvent event) {
        // getActivity().runOnUiThread(new Runnable() {
        // public void run() {
        rendererListAdapter.remove(event.device);
        // }});

    }

    @Subscribe
    public void onServerDeviceAdded(UpnpServerAddEvent event) {
        // getActivity().runOnUiThread(new Runnable() {
        // public void run() {
        if (serverListAdapter.getPosition(event.device) == -1)
            serverListAdapter.add(event.device);
        else
            serverListAdapter.notifyDataSetChanged();
        // }});
    }

    @Subscribe
    public void onServerDeviceRemoved(UpnpServerRemoveEvent event) {
        // getActivity().runOnUiThread(new Runnable() {
        // public void run() {
        serverListAdapter.remove(event.device);
        // }});
    }

    // private static final Logger log =
    // Logger.getLogger(BrowseActivity.class.getName());

    private DeviceAdapter serverListAdapter;
    private DeviceAdapter rendererListAdapter;

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
        ListView serverListView = (ListView) layout.findViewById(R.id.serverList);
        serverListAdapter = new DeviceAdapter(getActivity());
        View header = (View) inflater.inflate(R.layout.device_adapter_header, null);
        ((TextView) header.findViewById(R.id.deviceType)).setText("Librairie");
        serverListView.addHeaderView(header);

        if (UpnpDeviceManager.getInstance().lstServer.isEmpty()) {
            if (UpnpDeviceManager.getInstance().libraryDevice != null)
                serverListAdapter.add(UpnpDeviceManager.getInstance().libraryDevice);
        } else
            serverListAdapter.addAll(UpnpDeviceManager.getInstance().lstServer);
        serverListView.setAdapter(serverListAdapter);

        serverListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (id == -1)
                    return;

                DeviceDisplayHolder holder = (DeviceDisplayHolder) view.getTag();
                // UpnpServerDevice device = (UpnpServerDevice)
                // parent.getAdapter().getItem(position);
                // device.setSelected(true);
                // ((CheckedTextView)
                // view.findViewById(R.id.deviceName)).setChecked(true);
                // Application.libraryDevice = device;

                holder.device.setSelected(true);
                holder.deviceName.setChecked(true);

                UpnpDeviceManager.getInstance().setLibraryDevice((UpnpServerDevice) holder.device);

            }
        });
    }

    private void initRendererList(LayoutInflater inflater, View layout) {
        ListView rendererListView = (ListView) layout.findViewById(R.id.rendererList);
        rendererListAdapter = new DeviceAdapter(getActivity());
        View header = (View) inflater.inflate(R.layout.device_adapter_header, null);
        ((TextView) header.findViewById(R.id.deviceType)).setText("Lecteur");
        rendererListView.addHeaderView(header);
        // rendererListAdapter.addAll(UpnpService.lstRenderer);

        if (UpnpDeviceManager.getInstance().lstRenderer.isEmpty()) {
            if (UpnpDeviceManager.getInstance().rendererDevice != null)
                rendererListAdapter.addAll(UpnpDeviceManager.getInstance().rendererDevice);
        } else
            rendererListAdapter.addAll(UpnpDeviceManager.getInstance().lstRenderer);
        rendererListView.setAdapter(rendererListAdapter);

        rendererListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (id == -1)
                    return;
                // UpnpRendererDevice device = (UpnpRendererDevice)
                // parent.getAdapter().getItem(position);
                // device.setSelected(!device.isSelected());
                // ((CheckedTextView)
                // view.findViewById(R.id.deviceName)).setChecked(device.isSelected());
                // if (device.isSelected())
                // Application.rendererDevice.add(device);
                // else
                // Application.rendererDevice.remove(device);
                DeviceDisplayHolder holder = (DeviceDisplayHolder) view.getTag();

                holder.device.setSelected(!holder.device.isSelected());
                holder.deviceName.setChecked(holder.device.isSelected());
                if (holder.device.isSelected()) {
                    UpnpDeviceManager.getInstance().addRendererDevice((UpnpRendererDevice) holder.device);
                } else {
                    UpnpDeviceManager.getInstance().removeRendererDevice((UpnpRendererDevice) holder.device);

                }

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View layout = inflater.inflate(R.layout.activity_devicelists, null);

        initServerList(inflater, layout);

        initRendererList(inflater, layout);

        return layout;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
