package fr.fladajonesjones.MediaControler.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import fr.fladajonesjones.MediaControler.Application;
import fr.fladajonesjones.MediaControler.R;
import fr.fladajonesjones.MediaControler.adapter.RendererStatusGridAdapter;
import fr.fladajonesjones.MediaControler.manager.UpnpDeviceManager;
import fr.fladajonesjones.MediaControler.menu.MenuDrawerUtil;
import fr.fladajonesjones.MediaControler.upnp.UpnpRendererDevice;
import fr.fladajonesjones.MediaControler.upnp.UpnpServerDevice;
import fr.fladajonesjones.MediaControler.upnp.UpnpServiceClient;

public class RendererGridFragment extends Fragment implements UpnpServiceClient {

	   BroadcastReceiver mStatusListener = new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            String action = intent.getAction();
	            if (UpnpRendererDevice.STATUT_CHANGED.equals(action)) {
	            	rendererListAdapter.notifyDataSetChanged();
	        }
	    }
	        };
	
	@Override
	public void onRendererDeviceAdded(final UpnpRendererDevice renderer) {
		getActivity().runOnUiThread(new Runnable() {
		    public void run() {
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
	}

	@Override
	public void onServerDeviceRemoved(final UpnpServerDevice server) {
	}


	// private static final Logger log =
	// Logger.getLogger(BrowseActivity.class.getName());

	
	private RendererStatusGridAdapter rendererListAdapter;

	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		MenuDrawerUtil.toggleMenu();
		UpnpDeviceManager.getInstance().register(this);
		IntentFilter f = new IntentFilter();
        f.addAction(UpnpRendererDevice.STATUT_CHANGED);
        Application.instance.registerReceiver(mStatusListener, new IntentFilter(f));

	};


	private void initRendererList(LayoutInflater inflater, View layout) {
		GridView rendererListView = (GridView) layout
				.findViewById(R.id.rendererStatus_gridview);
		rendererListAdapter = new RendererStatusGridAdapter(getActivity());
		
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
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View layout = inflater.inflate(R.layout.fragment_selected_renderer_status_grid_view, null);

		initRendererList(inflater, layout);

		return layout;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		UpnpDeviceManager.getInstance().unregister(this);
        Application.instance.unregisterReceiver(mStatusListener);

	}
}
