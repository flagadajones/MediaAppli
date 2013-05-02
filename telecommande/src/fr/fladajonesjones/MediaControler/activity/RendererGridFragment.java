package fr.fladajonesjones.MediaControler.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.squareup.otto.Subscribe;

import fr.fladajonesjones.MediaControler.R;
import fr.fladajonesjones.MediaControler.adapter.RendererStatusGridAdapter;
import fr.fladajonesjones.MediaControler.events.UpnpRendererAddEvent;
import fr.fladajonesjones.MediaControler.events.UpnpRendererMetaChangeEvent;
import fr.fladajonesjones.MediaControler.events.UpnpRendererRemoveEvent;
import fr.fladajonesjones.MediaControler.events.UpnpRendererStatutChangeEvent;
import fr.fladajonesjones.MediaControler.manager.UpnpDeviceManager;
import fr.fladajonesjones.MediaControler.menu.MenuDrawerUtil;
import fr.flagadajones.media.util.BusManager;

public class RendererGridFragment extends Fragment {

	  
	
	        @Subscribe
	        public void onStatutChange(final UpnpRendererStatutChangeEvent event){
	        	  getActivity().runOnUiThread(new Runnable() {
	 	             public void run() {
	 	          
	        	rendererListAdapter.notifyDataSetChanged();
	 	            }});

	        }@Subscribe
	    	public void onMetaChanged(UpnpRendererMetaChangeEvent event){
	        	 getActivity().runOnUiThread(new Runnable() {
	 	             public void run() {
	 	          
	        	rendererListAdapter.notifyDataSetChanged();
	 	            }});
	    	}
	    	
	        
	        
	        @Subscribe
	        public void onRendererDeviceAdded(final UpnpRendererAddEvent event) {
	             getActivity().runOnUiThread(new Runnable() {
	             public void run() {
	            if (rendererListAdapter.getPosition(event.device) == -1)
	                rendererListAdapter.add(event.device);
	            else
	                rendererListAdapter.notifyDataSetChanged();
	             }});
	        }

	        @Subscribe
	        public void onRendererDeviceRemoved(final UpnpRendererRemoveEvent event) {
	            getActivity().runOnUiThread(new Runnable() {
	             public void run() {
	            rendererListAdapter.remove(event.device);
	             }});

	        }

	    
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


	// private static final Logger log =
	// Logger.getLogger(BrowseActivity.class.getName());

	
	private RendererStatusGridAdapter rendererListAdapter;

	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		MenuDrawerUtil.toggleMenu();

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

	}
}
