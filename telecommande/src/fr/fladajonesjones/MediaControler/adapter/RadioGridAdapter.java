package fr.fladajonesjones.MediaControler.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import fr.fladajonesjones.MediaControler.DialogRendererSelector;
import fr.fladajonesjones.MediaControler.R;
import fr.fladajonesjones.MediaControler.manager.UpnpDeviceManager;
import fr.fladajonesjones.MediaControler.upnp.UpnpRendererDevice;
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
            holder.radioName = (TextView) row.findViewById(R.id.radioName);
            row.setTag(holder);
        } else {
            holder = (RadioHolder) row.getTag();
        }
        final Radio item = getItem(position);


        Picasso.with(getContext()).load(item.albumArt).placeholder(R.drawable.stub)
                .error(R.drawable.bg_img_notfound3).resize(80, 80).into(holder.radioName);

        final View it = row;
        row.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                // dans le cas d'un clique sur une radio, on affiche la fenetre de selection du renderer si on a plus
                // d'un renderer de disponible
                // createDialogSelectionDevice(radio, (ImageView) v.findViewById(R.id.radioIcone));
                if (UpnpDeviceManager.getInstance().rendererDevice.size() == 0) {
                    return;
                } else if (UpnpDeviceManager.getInstance().getDefaultRenderer() != null) {
                    UpnpDeviceManager.getInstance().getDefaultRenderer().playMusique(item);
                } else if (UpnpDeviceManager.getInstance().rendererDevice.size() == 1) {
                    UpnpRendererDevice renderer = UpnpDeviceManager.getInstance().rendererDevice.get(0);
                    renderer.playMusique(item);
                } else {
                    DialogRendererSelector.createDialogSelectionDevice((Activity) getContext(), item,
                            ((TextView) it.findViewById(R.id.radioName)).getCompoundDrawables()[1]);
                }
            }
        });

        //Application.imageLoader.DisplayImage(item.albumArt, holder.radioIcone);


        holder.radioName.setText(item.titre);

        return row;
    }

    static class RadioHolder {
        TextView radioName;
    }
}
