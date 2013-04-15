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
import fr.fladajonesjones.MediaControler.upnp.UpnpRendererDevice;
import fr.fladajonesjones.media.model.Musique;

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
			row = inflater.inflate(R.layout.renderer_status_playing_grid_item,
					null, false);
			ImageView deviceIcone = (ImageView) row
					.findViewById(R.id.deviceIcone);
			TextView deviceName = (TextView) row.findViewById(R.id.deviceName);

			ImageView albumArt = (ImageView) row.findViewById(R.id.rendererStatusAlbumArt);
			TextView artisteName = (TextView) row
					.findViewById(R.id.rendererStatusArtisteName);
			TextView albumName = (TextView) row.findViewById(R.id.rendererStatusSongName);
			SeekBar positionPiste = (SeekBar) row
					.findViewById(R.id.rendererStatusPositionPiste);

			deviceName.setText(item.getName());
			Application.imageLoader.DisplayImage(item.icone, deviceIcone);
			Musique musique = item.getMusique();
			Application.imageLoader.DisplayImage(musique.albumArt, albumArt);
			artisteName.setText("artiste");
			albumName.setText(musique.titre);
			positionPiste.setMax(10);
			positionPiste.setProgress(5);

		} else {
			if (item.isConnected())
				row = inflater
						.inflate(R.layout.renderer_status_nomedia_grid_item,
								null, false);
			else
				row = inflater.inflate(
						R.layout.renderer_status_notconnected_grid_item, null,
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