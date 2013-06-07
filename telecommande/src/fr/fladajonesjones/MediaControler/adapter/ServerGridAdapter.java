package fr.fladajonesjones.MediaControler.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import fr.fladajonesjones.MediaControler.R;
import fr.fladajonesjones.MediaControler.manager.UpnpDeviceManager;
import fr.fladajonesjones.MediaControler.upnp.UpnpDevice;
import fr.fladajonesjones.MediaControler.upnp.UpnpServerDevice;

public class ServerGridAdapter extends ArrayAdapter<UpnpServerDevice> {

    private LayoutInflater inflater;

    public ServerGridAdapter(Context context) {
        super(context, -1);

        inflater = ((Activity) context).getLayoutInflater();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        DeviceDisplayHolder holder = null;

        if (row == null) {
            row = inflater.inflate(R.layout.server_grid_item, null, false);

            holder = new DeviceDisplayHolder();
            holder.deviceStrip = (ImageView) row.findViewById(R.id.stripe);
            holder.deviceIcone = (ImageView) row.findViewById(R.id.deviceIcone);
            holder.deviceName = (TextView) row.findViewById(R.id.deviceName);

            // holder.deviceHydrate=(TextView)row.findViewById(R.id.deviceHydrate);
            row.setTag(holder);
        } else {
            holder = (DeviceDisplayHolder) row.getTag();
        }
        final UpnpServerDevice server = getItem(position);


        if (server.getDevice() != null) {
            holder.deviceName.setText(server.getName()
                    + (server.getDevice().isFullyHydrated() ? "*" : ""));
        } else {
            holder.deviceName.setText(server.getName());
        }
        // url=item.getDevice().getDetails().getPresentationURI().toString()+item.getDevice().getIcons()[0].getUri().toString().substring(1);

        if (server.icone != null && server.icone != "")
            Picasso.with(getContext()).load(server.icone).placeholder(R.drawable.stub)
                    .error(R.drawable.bg_img_notfound).into(holder.deviceIcone);

        //Application.imageLoader.DisplayImage(server.icone, holder.deviceIcone);

        if (server.isSelected()) {
            holder.deviceStrip.setBackgroundColor(getContext().getResources().getColor(R.color.blue));
        } else {
            holder.deviceStrip.setBackgroundColor(getContext().getResources().getColor(R.color.red));
        }


        //holder.deviceName.setChecked(item.isSelected());
        holder.device = server;


        ImageView overflowMenu = (ImageView) row.findViewById(R.id.overflow);

        overflowMenu.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                PopupMenu popup = new PopupMenu(ServerGridAdapter.this.getContext(), v);
                popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.action_connect:
                                server.setSelected(true);
                                if (UpnpDeviceManager.getInstance().getLibraryDevice() != null)
                                    UpnpDeviceManager.getInstance().getLibraryDevice().setSelected(false);
                                UpnpDeviceManager.getInstance().setLibraryDevice(server);
                                ServerGridAdapter.this.notifyDataSetChanged();
                                return true;
                            case R.id.action_disconnect:
                                server.setSelected(false);
                                UpnpDeviceManager.getInstance().setLibraryDevice(null);
                                ServerGridAdapter.this.notifyDataSetChanged();
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

    public static class DeviceDisplayHolder {
        public ImageView deviceStrip;
        public ImageView deviceIcone;
        public TextView deviceName;
        public UpnpDevice device;

        // TextView deviceHydrate;
    }
}
