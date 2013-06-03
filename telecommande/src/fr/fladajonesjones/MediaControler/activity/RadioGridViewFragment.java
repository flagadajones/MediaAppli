package fr.fladajonesjones.MediaControler.activity;

import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import fr.fladajonesjones.MediaControler.R;
import fr.fladajonesjones.MediaControler.adapter.RadioGridAdapter;
import fr.fladajonesjones.MediaControler.manager.RadioManager;
import fr.fladajonesjones.MediaControler.menu.MenuDrawerUtil;
import fr.fladajonesjones.media.model.Radio;

public class RadioGridViewFragment extends Fragment {

    LayoutInflater inflater = null;
    RadioGridAdapter radioGridAdapter = null;
    RadioGridAdapter radioGridAdapterFav = null;

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MenuDrawerUtil.toggleMenu();
    };

    private void initGridRadio(View layout) {
        final GridView gridView = (GridView) layout.findViewById(R.id.radio_gridview);
        final GridView gridViewFav = (GridView) layout.findViewById(R.id.radioFav_gridview);

        radioGridAdapter = new RadioGridAdapter(getActivity());
        radioGridAdapterFav = new RadioGridAdapter(getActivity());
        gridView.setAdapter(radioGridAdapter);
        gridViewFav.setAdapter(radioGridAdapterFav);

        final RadioManager radioManager = new RadioManager(getActivity());

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

        new AsyncTask<Void, Void, List<Radio>>() {
            @Override
            protected void onPostExecute(List<Radio> result) {
                if (result.isEmpty()) {
                    gridViewFav.setVisibility(View.GONE);
                } else {
                    gridViewFav.setVisibility(View.VISIBLE);
                    radioGridAdapterFav.addAll(result);
                }

            }

            @Override
            protected List<Radio> doInBackground(Void... params) {
                return radioManager.getFav(5);
            }

        }.execute();


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
