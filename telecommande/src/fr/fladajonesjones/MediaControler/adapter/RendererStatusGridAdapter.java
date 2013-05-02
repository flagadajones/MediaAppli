package fr.fladajonesjones.MediaControler.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import fr.fladajonesjones.MediaControler.Application;
import fr.fladajonesjones.MediaControler.R;
import fr.fladajonesjones.MediaControler.events.UpnpRendererMetaChangeEvent;
import fr.fladajonesjones.MediaControler.upnp.UpnpRendererDevice;
import fr.fladajonesjones.media.model.Musique;
import fr.flagadajones.media.util.BusManager;

public class RendererStatusGridAdapter extends ArrayAdapter<UpnpRendererDevice> {

	public RendererStatusGridAdapter(Context context) {
		super(context, -1);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;

		UpnpRendererDevice item = getItem(position);

		LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();

		if (item.isPlaying()) {
			row = inflater.inflate(R.layout.renderer_grid_item_status_playing,
					null, false);
			ImageView deviceIcone = (ImageView) row
					.findViewById(R.id.deviceIcone);
			TextView deviceName = (TextView) row.findViewById(R.id.deviceName);

			ImageView albumArt = (ImageView) row
					.findViewById(R.id.rendererStatusAlbumArt);
			TextView artisteName = (TextView) row
					.findViewById(R.id.rendererStatusArtisteName);
			TextView albumName = (TextView) row
					.findViewById(R.id.rendererStatusSongName);
			SeekBar positionPiste = (SeekBar) row
					.findViewById(R.id.rendererStatusPositionPiste);

			deviceName.setText(item.getName());
			Application.imageLoader.DisplayImage(item.icone, deviceIcone);
			artisteName.setText("artiste");
			Musique musique = item.getMusique();
			if (musique != null) {
				Application.imageLoader
						.DisplayImage(musique.albumArt, albumArt);

				albumName.setText(musique.titre);
			}
			positionPiste.setMax(new Long(item.positionInfo
					.getTrackDurationSeconds()).intValue());
			positionPiste.setProgress(item.positionInfo.getRelCount());

		} else {
			if (item.isConnected())
				row = inflater
						.inflate(R.layout.renderer_grid_item_status_nomedia,
								null, false);
			else
				row = inflater.inflate(
						R.layout.renderer_grid_item_status_notconnected, null,
						false);
			ImageView deviceIcone = (ImageView) row
					.findViewById(R.id.deviceIcone);
			TextView deviceName = (TextView) row.findViewById(R.id.deviceName);
			deviceName.setText(item.getName());
			Application.imageLoader.DisplayImage(item.icone, deviceIcone);

		}
		// holder.deviceName.setText("lecteur");
		return row;
	}

	static class DeviceDisplayHolder {
		ImageView deviceIcone;
		TextView deviceName;
	}
}