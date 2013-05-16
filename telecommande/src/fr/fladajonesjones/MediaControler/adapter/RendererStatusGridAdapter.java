package fr.fladajonesjones.MediaControler.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.PopupMenu.OnMenuItemClickListener;
import com.squareup.picasso.Picasso;
import fr.fladajonesjones.MediaControler.Application;
import fr.fladajonesjones.MediaControler.R;
import fr.fladajonesjones.MediaControler.activity.NowPlayingFragment;
import fr.fladajonesjones.MediaControler.adapter.ServerGridAdapter.DeviceDisplayHolder;
import fr.fladajonesjones.MediaControler.manager.UpnpDeviceManager;
import fr.fladajonesjones.MediaControler.upnp.UpnpDevice;
import fr.fladajonesjones.MediaControler.upnp.UpnpRendererDevice;
import fr.fladajonesjones.media.model.Album;
import fr.fladajonesjones.media.model.Musique;

public class RendererStatusGridAdapter extends ArrayAdapter<UpnpRendererDevice> {

    public RendererStatusGridAdapter(Context context) {
        super(context, -1);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;

        final UpnpRendererDevice renderer = getItem(position);

        LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();

        if (renderer.isPlaying()) {
            row = inflater.inflate(R.layout.renderer_grid_item_status_playing, null, false);
            ImageView deviceIcone = (ImageView) row.findViewById(R.id.deviceIcone);
            TextView deviceName = (TextView) row.findViewById(R.id.deviceName);

            ImageView albumArt = (ImageView) row.findViewById(R.id.rendererStatusAlbumArt);
            TextView artisteName = (TextView) row.findViewById(R.id.rendererStatusArtisteName);
            TextView albumName = (TextView) row.findViewById(R.id.rendererStatusSongName);
            ProgressBar positionPiste = (ProgressBar) row.findViewById(R.id.rendererStatusPositionPiste);

            deviceName.setText(renderer.getName());
            Picasso.with(getContext()).load(renderer.icone).placeholder(R.drawable.stub)
                    .error(R.drawable.bg_img_notfound).into(deviceIcone);

         //   Application.imageLoader.DisplayImage(renderer.icone, deviceIcone);
            artisteName.setText("artiste");
            Musique musique = renderer.getMusique();
            if (musique != null) {
                Picasso.with(getContext()).load(musique.albumArt).placeholder(R.drawable.stub)
                        .error(R.drawable.bg_img_notfound).into(albumArt);

              //  Application.imageLoader.DisplayImage(musique.albumArt, albumArt);

                albumName.setText(musique.titre);
                if(renderer.positionInfo!=null && renderer.getMusique() instanceof Album){
                    albumName.setText(albumName.getText()+" - "+((Album)renderer.getMusique()).getPistes().get(renderer.positionInfo.getTrack().getValue().intValue()).titre);
                }
            }
            if(renderer.positionInfo!=null){
            positionPiste.setMax(new Long(renderer.positionInfo.getTrackDurationSeconds()).intValue());
                positionPiste.setProgress(renderer.positionInfo.getRelCount());
            }
            else{
            positionPiste.setMax(100);
                                                                               positionPiste.setProgress(0);

            }
            LinearLayout layout = (LinearLayout) row.findViewById(R.id.contentLayout);
            layout.setClickable(true);
            layout.setFocusable(true);
            layout.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    NowPlayingFragment fragment = new NowPlayingFragment();
                    fragment.renderer = renderer;
                    replaceFragment(fragment); // TODO Auto-generated method stub

                }
            });

        } else {
            if (renderer.isConnected())
                row = inflater.inflate(R.layout.renderer_grid_item_status_nomedia, null, false);
            else
                row = inflater.inflate(R.layout.renderer_grid_item_status_notconnected, null, false);
            ImageView deviceIcone = (ImageView) row.findViewById(R.id.deviceIcone);
            TextView deviceName = (TextView) row.findViewById(R.id.deviceName);
            deviceName.setText(renderer.getName());
            if(renderer.icone!=null)
            Picasso.with(getContext()).load(renderer.icone).placeholder(R.drawable.stub)
                    .error(R.drawable.bg_img_notfound).into(deviceIcone);

          //  Application.imageLoader.DisplayImage(renderer.icone, deviceIcone);

        }

        // holder.deviceName.setText("lecteur");

        ImageView overflowMenu = (ImageView) row.findViewById(R.id.overflow);

        ImageView deviceStrip = (ImageView) row.findViewById(R.id.stripe);

        if (renderer.isSelected() && renderer.isConnected()) {
            deviceStrip.setBackgroundColor(getContext().getResources().getColor(R.color.green));
        } else if (renderer.isSelected()) {
            deviceStrip.setBackgroundColor(getContext().getResources().getColor(R.color.blue));
        } else {
            deviceStrip.setBackgroundColor(getContext().getResources().getColor(R.color.red));
        }

        overflowMenu.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(RendererStatusGridAdapter.this.getContext(), v);
                popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                        case R.id.action_connect:
                            renderer.setSelected(true);
                            UpnpDeviceManager.getInstance().addRendererDevice(renderer);
                            RendererStatusGridAdapter.this.notifyDataSetChanged();
                            return true;
                        case R.id.action_disconnect:
                            renderer.setSelected(false);
                            UpnpDeviceManager.getInstance().removeRendererDevice(renderer);
                            RendererStatusGridAdapter.this.notifyDataSetChanged();
                            return true;
                        default:
                            return false;
                        }
                    }
                });
                popup.inflate(R.menu.renderer_device_overflow);

                popup.show();

            }
        });

        return row;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction ft = ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.details, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }
}
