package fr.fladajonesjones.MediaControler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import fr.fladajonesjones.MediaControler.adapter.DeviceGridAdapter;
import fr.fladajonesjones.MediaControler.manager.UpnpDeviceManager;
import fr.fladajonesjones.MediaControler.model.Album;
import fr.fladajonesjones.MediaControler.model.Musique;
import fr.fladajonesjones.MediaControler.upnp.UpnpRendererDevice;
import fr.fladajonesjones.MediaControler.upnp.UpnpServerDevice;

public class DialogRendererSelector {

    static UpnpRendererDevice selectedDevice = null;
    static public Album album;
    static BroadcastReceiver mStatusListener = new BroadcastReceiver() {
        
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
           
            if (UpnpServerDevice.LOADING_PISTE_OK.equals(action)) {
                    Application.instance.unregisterReceiver(mStatusListener);
             //       Application.activity.showToast("Chargement Piste ok", true);
                    selectedDevice.playMusique(album);
                }
            }
        
    };

    public static void createDialogSelectionDevice(Activity activity, final Musique musique, Drawable drawable) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View contentView = inflater.inflate(R.layout.popup_play_selector, null);
        DialogRendererSelector.album = (Album)musique;
        final DeviceGridAdapter deviceGridAdapter = new DeviceGridAdapter(activity);

        deviceGridAdapter.addAll(UpnpDeviceManager.getInstance().rendererDevice);

        final GridView gridDevice = (GridView) contentView.findViewById(R.id.popupRenderer_gridview);

        gridDevice.setAdapter(deviceGridAdapter);
        selectedDevice = null;

        gridDevice.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // gridDevice.setSelection(position);
                v.setSelected(true);
                selectedDevice = (UpnpRendererDevice) deviceGridAdapter.getItem(position);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(contentView)
        .setIcon(Util.resize(drawable))
        .setTitle(album.nom)
        .setCancelable(true)
                .setPositiveButton("Toujours", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        UpnpDeviceManager.getInstance().setDefaultRenderer(selectedDevice);
                        playMusique();
                    }

                }).setNegativeButton("Une Seule Fois", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // dialog.cancel();
                        playMusique();

                    }
                });
        AlertDialog alert = builder.create();
        alert.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        alert.show();

    }

    private static void playMusique(){
       
        if (album.isPisteLoaded()) {
            selectedDevice.playMusique(album);
        } else {
            IntentFilter f = new IntentFilter();
            f.addAction(UpnpServerDevice.LOADING_PISTE_OK);
            Application.instance.registerReceiver(mStatusListener, new IntentFilter(f));

           UpnpDeviceManager.getInstance().libraryDevice.loadPiste(album.upnpId);
        }
        
    }
}
