
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
import fr.fladajonesjones.MediaControler.R;
import fr.fladajonesjones.MediaControler.upnp.UpnpDevice;

public class DeviceGridAdapter extends ArrayAdapter<UpnpDevice> {


    public DeviceGridAdapter(Context context) {
        super(context, -1);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        DeviceDisplayHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            row = inflater.inflate(R.layout.popup_renderer_grid_item, null, false);

            holder = new DeviceDisplayHolder();
            holder.deviceIcone = (ImageView) row.findViewById(R.id.deviceIcone);
            holder.deviceName = (TextView) row.findViewById(R.id.deviceName);
            row.setTag(holder);
        } else {
            holder = (DeviceDisplayHolder) row.getTag();
        }
        UpnpDevice item = getItem(position);


        if (item.icone != null)
            Picasso.with(getContext()).load(item.icone).placeholder(R.drawable.stub)
                    .error(R.drawable.bg_img_notfound).into(holder.deviceIcone);


        // Application.imageLoader.DisplayImage(item.icone, holder.deviceIcone);

        holder.deviceName.setText(item.getName());
        // holder.deviceName.setText("lecteur");
        return row;
    }

    static class DeviceDisplayHolder {
        ImageView deviceIcone;
        TextView deviceName;
    }
}