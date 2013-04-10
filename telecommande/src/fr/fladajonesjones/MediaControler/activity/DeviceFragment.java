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
import fr.fladajonesjones.MediaControler.R;
import fr.fladajonesjones.MediaControler.adapter.DeviceAdapter;
import fr.fladajonesjones.MediaControler.adapter.DeviceAdapter.DeviceDisplayHolder;
import fr.fladajonesjones.MediaControler.manager.UpnpDeviceManager;
import fr.fladajonesjones.MediaControler.menu.MenuDrawerUtil;
import fr.fladajonesjones.MediaControler.upnp.UpnpRendererDevice;
import fr.fladajonesjones.MediaControler.upnp.UpnpServerDevice;
import fr.fladajonesjones.MediaControler.upnp.UpnpServiceClient;

public class DeviceFragment extends Fragment implements UpnpServiceClient {

	@Override
	public void onRendererDeviceAdded(final UpnpRendererDevice renderer) {
		getActivity().runOnUiThread(new Runnable() {
		    public void run() {
		// TODO Auto-generated method stub
		if (rendererListAdapter.getPosition(renderer) == -1)
			rendererListAdapter.add(renderer);
		else
			rendererListAdapter.notifyDataSetChanged();
		    }});
	}

	@Override
	public void onRendererDeviceRemoved(final UpnpRendererDevice renderer) {
		getActivity().runOnUiThread(new Runnable() {
		    public void run() {
		    	rendererListAdapter.remove(renderer);
		    }});

	}

	@Override
	public void onServerDeviceAdded(final UpnpServerDevice server) {
		getActivity().runOnUiThread(new Runnable() {
		    public void run() {
		if (serverListAdapter.getPosition(server) == -1)
			serverListAdapter.add(server);
		else
			serverListAdapter.notifyDataSetChanged();
		    }});
	}

	@Override
	public void onServerDeviceRemoved(final UpnpServerDevice server) {
		getActivity().runOnUiThread(new Runnable() {
		    public void run() {
		    	serverListAdapter.remove(server);
		    }
		});
	}

	// private static final Logger log =
	// Logger.getLogger(BrowseActivity.class.getName());

	private DeviceAdapter serverListAdapter;
	private DeviceAdapter rendererListAdapter;

	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		MenuDrawerUtil.toggleMenu();
		UpnpDeviceManager.getInstance().register(this);
	};

	private void initServerList(LayoutInflater inflater, View layout) {
		ListView serverListView = (ListView) layout
				.findViewById(R.id.serverList);
		serverListAdapter = new DeviceAdapter(getActivity());
		View header = (View) inflater.inflate(R.layout.device_adapter_header,
				null);
		((TextView) header.findViewById(R.id.deviceType)).setText("Librairie");
		serverListView.addHeaderView(header);

		if (UpnpDeviceManager.getInstance().lstServer.isEmpty()) {
			if (UpnpDeviceManager.getInstance().libraryDevice != null)
				serverListAdapter
						.add(UpnpDeviceManager.getInstance().libraryDevice);
		} else
			serverListAdapter.addAll(UpnpDeviceManager.getInstance().lstServer);
		serverListView.setAdapter(serverListAdapter);

		serverListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (id == -1)
					return;

				DeviceDisplayHolder holder = (DeviceDisplayHolder) view
						.getTag();
				// UpnpServerDevice device = (UpnpServerDevice)
				// parent.getAdapter().getItem(position);
				// device.setSelected(true);
				// ((CheckedTextView)
				// view.findViewById(R.id.deviceName)).setChecked(true);
				// Application.libraryDevice = device;

				holder.device.setSelected(true);
				holder.deviceName.setChecked(true);

				UpnpDeviceManager.getInstance().setLibraryDevice(
						(UpnpServerDevice) holder.device);

			}
		});
	}

	private void initRendererList(LayoutInflater inflater, View layout) {
		ListView rendererListView = (ListView) layout
				.findViewById(R.id.rendererList);
		rendererListAdapter = new DeviceAdapter(getActivity());
		View header = (View) inflater.inflate(R.layout.device_adapter_header,
				null);
		((TextView) header.findViewById(R.id.deviceType)).setText("Lecteur");
		rendererListView.addHeaderView(header);
		// rendererListAdapter.addAll(UpnpService.lstRenderer);

		if (UpnpDeviceManager.getInstance().lstRenderer.isEmpty()) {
			if (UpnpDeviceManager.getInstance().rendererDevice != null)
				rendererListAdapter
						.addAll(UpnpDeviceManager.getInstance().rendererDevice);
		} else
			rendererListAdapter
					.addAll(UpnpDeviceManager.getInstance().lstRenderer);
		rendererListView.setAdapter(rendererListAdapter);

		rendererListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
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
				DeviceDisplayHolder holder = (DeviceDisplayHolder) view
						.getTag();

				holder.device.setSelected(!holder.device.isSelected());
				holder.deviceName.setChecked(holder.device.isSelected());
				if (holder.device.isSelected()) {
					UpnpDeviceManager.getInstance().addRendererDevice(
							(UpnpRendererDevice) holder.device);
				} else {
					UpnpDeviceManager.getInstance().removeRendererDevice(
							(UpnpRendererDevice) holder.device);

				}

			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View layout = inflater.inflate(R.layout.activity_devicelists, null);

		initServerList(inflater, layout);

		initRendererList(inflater, layout);

		return layout;
	}

	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		UpnpDeviceManager.getInstance().unregister(this);
	}
}
