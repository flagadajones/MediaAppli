package fr.fladajonesjones.MediaControler.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import fr.fladajonesjones.MediaControler.Application;
import fr.fladajonesjones.MediaControler.R;
import fr.fladajonesjones.MediaControler.adapter.RowGridAdapter;
import fr.fladajonesjones.MediaControler.database.AlbumDAO;
import fr.fladajonesjones.MediaControler.menu.MenuDrawerUtil;
import fr.fladajonesjones.MediaControler.model.Album;
import fr.fladajonesjones.MediaControler.model.Row;
import fr.fladajonesjones.MediaControler.model.Row.RowArtiste;
import fr.fladajonesjones.MediaControler.upnp.UpnpServerDevice;

public class AlbumFragment extends Fragment {
    LayoutInflater inflater;
    RowGridAdapter rowGridAdapter;
    ProgressDialog progressDialog;
    AlbumDAO albumDao = new AlbumDAO();

    BroadcastReceiver mStatusListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UpnpServerDevice.FIND_ALBUM.equals(action)) {

                // Album album=intent.getParcelableExtra("album");
                rowGridAdapter.clear();
                rowGridAdapter.addAll(initRow(albumDao.getAllAlbums(true, 0)));
                rowGridAdapter.notifyDataSetChanged();

            } else if (UpnpServerDevice.LOADING_PISTE.equals(action)) {
                progressDialog.show();

            } else if (UpnpServerDevice.LOADING_PISTE_OK.equals(action)) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

        }
    };

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MenuDrawerUtil.toggleMenu();
        progressDialog= new ProgressDialog(getActivity());
        progressDialog.setTitle("Patientez");
        progressDialog.setMessage("Chargement des pistes...");
        progressDialog.setIndeterminate(true);
        // Application.libraryDevice.browseAlbums();
    };

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View layout = inflater.inflate(R.layout.activity_album_list_view, null);
        this.inflater = inflater;

        initGridAlbum(layout);

        IntentFilter f = new IntentFilter();
        f.addAction(UpnpServerDevice.FIND_ALBUM);
        f.addAction(UpnpServerDevice.LOADING_PISTE);
        f.addAction(UpnpServerDevice.LOADING_PISTE_OK);
        Application.instance.registerReceiver(mStatusListener, new IntentFilter(f));

        return layout;
    }

    private void initGridAlbum(View layout) {
        rowGridAdapter = new RowGridAdapter(getActivity());

        ListView listView = (ListView) layout.findViewById(R.id.artiste_listview);

        listView.setFastScrollEnabled(true);
        listView.setAdapter(rowGridAdapter);

        // Album album=intent.getParcelableExtra("album");
        rowGridAdapter.addAll(initRow(albumDao.getAllAlbums(true, 15)));
        rowGridAdapter.notifyDataSetChanged();

        new AsyncTask<Void, Void, List<Row>>() {
            @Override
            protected void onPostExecute(List<Row> result) {
                rowGridAdapter.clear();
                rowGridAdapter.addAll(result);
                rowGridAdapter.notifyDataSetChanged();
            }

            @Override
            protected List<Row> doInBackground(Void... params) {
                return initRow(albumDao.getAllAlbums(true, 0));
            }
        }.execute();

        // rowGridAdapter.addAll(initRow(albumDao.getAllAlbums(true)));
        // rowGridAdapter.notifyDataSetChanged();

    }

    private List<Row> initRow(List<Album> lstAlbum) {
        List<Row> liste = new ArrayList<Row>();
        String[] allColors = getActivity().getResources().getStringArray(R.array.colors);

        int colorId = 0;
        int count = 0;
        Row row = new Row();
        liste.add(row);
        boolean newA = true;
        RowArtiste rowArtiste = null;

        int artisteId = -1;
        for (Album album : lstAlbum) {

            if (count == 5) {
                count = 0;
                artisteId = -1;
                row = new Row();
                liste.add(row);
            }
            if (artisteId != album.artisteId && artisteId != -1) {
                colorId++;
                if (colorId > allColors.length - 1) {
                    colorId = 0;
                }
            }
            if (artisteId != album.artisteId) {
                rowArtiste = new RowArtiste();
                rowArtiste.artiste = album.artiste;
                rowArtiste.color = Color.parseColor(allColors[colorId]);
                row.lstArtiste.add(rowArtiste);
                artisteId = album.artisteId;
            }
            row.lstAlbum.add(album);

            rowArtiste.nbAlbum++;
            count++;
        }

        if (liste.get(liste.size() - 1).lstArtiste.size() == 0) {
            liste.remove(liste.size() - 1);
        }

        return liste;
    }
    
    @Override
    public void onDestroyView() {
    	// TODO Auto-generated method stub
    	super.onDestroyView();
    	Application.instance.unregisterReceiver(mStatusListener);
    }

}
