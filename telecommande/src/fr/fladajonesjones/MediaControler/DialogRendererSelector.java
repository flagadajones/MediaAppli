package fr.fladajonesjones.MediaControler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.squareup.otto.Subscribe;

import fr.fladajonesjones.MediaControler.adapter.DeviceGridAdapter;
import fr.fladajonesjones.MediaControler.events.UpnpServerLoadingPisteOkEvent;
import fr.fladajonesjones.MediaControler.manager.UpnpDeviceManager;
import fr.fladajonesjones.MediaControler.upnp.UpnpRendererDevice;
import fr.fladajonesjones.media.model.Album;
import fr.fladajonesjones.media.model.Musique;
import fr.flagadajones.media.util.BusManager;

public class DialogRendererSelector {

    static UpnpRendererDevice selectedDevice = null;
    static public Musique musique;

    static Object eventSub = new Object() {
        @Subscribe
        public void onUpnpServerBrowseOk(UpnpServerLoadingPisteOkEvent event) {
            BusManager.getInstance().unregister(this);

            selectedDevice.playMusique(musique);
        }
    };

    public static void createDialogSelectionDevice(Activity activity, final Musique musique, Drawable drawable) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View contentView = inflater.inflate(R.layout.popup_play_selector, null);
        DialogRendererSelector.musique = musique;
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
        builder.setView(contentView)/*.setIcon(drawable)*/.setTitle(musique.titre).setCancelable(true)
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

    private static void playMusique() {
       if(selectedDevice!=null){
        if(musique instanceof Album) {
        if (((Album)musique).isPisteLoaded()) {
            selectedDevice.playMusique(musique);
        } else {
            BusManager.getInstance().register(eventSub);
            UpnpDeviceManager.getInstance().libraryDevice.loadPiste(musique.upnpId);
        }
        }
        else
            selectedDevice.playMusique(musique);

       }
    }
}
