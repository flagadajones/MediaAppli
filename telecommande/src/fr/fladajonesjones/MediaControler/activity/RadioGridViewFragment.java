package fr.fladajonesjones.MediaControler.activity;

import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import fr.fladajonesjones.MediaControler.DialogRendererSelector;
import fr.fladajonesjones.MediaControler.R;
import fr.fladajonesjones.MediaControler.adapter.RadioGridAdapter;
import fr.fladajonesjones.MediaControler.manager.RadioManager;
import fr.fladajonesjones.MediaControler.manager.UpnpDeviceManager;
import fr.fladajonesjones.MediaControler.menu.MenuDrawerUtil;
import fr.fladajonesjones.MediaControler.upnp.UpnpRendererDevice;
import fr.fladajonesjones.media.model.Radio;

public class RadioGridViewFragment extends Fragment {

    LayoutInflater inflater = null;
    RadioGridAdapter radioGridAdapter = null;

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MenuDrawerUtil.toggleMenu();
    };

    private void initGridRadio(View layout) {
        radioGridAdapter = new RadioGridAdapter(getActivity());

        final RadioManager radioManager = new RadioManager(getActivity());
        // radioManager.parse();

        new AsyncTask<Void, Void, List<Radio>>() {
            @Override
            protected void onPostExecute(List<Radio> result) {
                radioGridAdapter.addAll(result);
            }

            @Override
            protected List<Radio> doInBackground(Void... params) {
                return radioManager.parse();
            }
        }.execute();

        GridView gridView = (GridView) layout.findViewById(R.id.radio_gridview);
        gridView.setAdapter(radioGridAdapter);
        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // dans le cas d'un clique sur une radio, on affiche la fenetre de selection du renderer si on a plus
                // d'un renderer de disponible
                final Radio radio = (Radio) radioGridAdapter.getItem(position);
                // createDialogSelectionDevice(radio, (ImageView) v.findViewById(R.id.radioIcone));
                if (UpnpDeviceManager.getInstance().rendererDevice.size() == 0) {
                    return;
                } else if (UpnpDeviceManager.getInstance().getDefaultRenderer() != null) {
                    UpnpDeviceManager.getInstance().getDefaultRenderer().playMusique(radio);
                } else if (UpnpDeviceManager.getInstance().rendererDevice.size() == 1) {
                    UpnpRendererDevice renderer = UpnpDeviceManager.getInstance().rendererDevice.get(0);
                    renderer.playMusique(radio);
                } else {
                    DialogRendererSelector.createDialogSelectionDevice(getActivity(), radio,
                            ((ImageView) v.findViewById(R.id.radioIcone)).getDrawable());
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View layout = inflater.inflate(R.layout.fragment_radio_grid_view, null);
        this.inflater = inflater;

        initGridRadio(layout);

        return layout;
    }

}
