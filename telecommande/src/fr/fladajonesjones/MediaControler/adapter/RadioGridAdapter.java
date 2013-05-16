package fr.fladajonesjones.MediaControler.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import fr.fladajonesjones.MediaControler.Application;
import fr.fladajonesjones.MediaControler.R;
import fr.fladajonesjones.media.model.Radio;

public class RadioGridAdapter extends ArrayAdapter<Radio> {

	

	// Constructor
	public RadioGridAdapter(Context context) {
		super(context, -1);

			
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;
		RadioHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) getContext())
					.getLayoutInflater();
			row = inflater.inflate(R.layout.radio_grid_item, null, false);

			holder = new RadioHolder();
			holder.radioIcone = (ImageView) row.findViewById(R.id.radioIcone);
			holder.radioName = (TextView) row.findViewById(R.id.radioName);
			row.setTag(holder);
		} else {
			holder = (RadioHolder) row.getTag();
		}
		Radio item = getItem(position);


        Picasso.with(getContext()).load(item.albumArt).placeholder(R.drawable.stub)
                .error(R.drawable.bg_img_notfound).into(holder.radioIcone);

        //Application.imageLoader.DisplayImage(item.albumArt, holder.radioIcone);

		
		holder.radioName.setText(item.titre);

		return row;
	}

	static class RadioHolder {
		ImageView radioIcone;
		TextView radioName;
	}
}
