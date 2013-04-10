package fr.fladajonesjones.MediaControler.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import fr.fladajonesjones.MediaControler.Application;
import fr.fladajonesjones.MediaControler.R;
import fr.fladajonesjones.MediaControler.upnp.UpnpDevice;

public class DeviceAdapter extends ArrayAdapter<UpnpDevice> {

    private LayoutInflater inflater;

    public DeviceAdapter(Context context) {
        super(context, -1);

        inflater = ((Activity) context).getLayoutInflater();
  
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        DeviceDisplayHolder holder = null;

        if (row == null) {
            row = inflater.inflate(R.layout.device_adapter_row, null, false);

            holder = new DeviceDisplayHolder();
            holder.deviceIcone = (ImageView) row.findViewById(R.id.deviceIcone);
            holder.deviceName = (CheckedTextView) row.findViewById(R.id.deviceName);
            
            // holder.deviceHydrate=(TextView)row.findViewById(R.id.deviceHydrate);
            row.setTag(holder);
        } else {
            holder = (DeviceDisplayHolder) row.getTag();
        }
        UpnpDevice item = getItem(position);

       
        if(item.getDevice()!=null){
        holder.deviceName.setText(item.getName()
                + (item.getDevice().isFullyHydrated() ? "*" : ""));
        }
        else{
        	holder.deviceName.setText(item.getName());	
        }
        // url=item.getDevice().getDetails().getPresentationURI().toString()+item.getDevice().getIcons()[0].getUri().toString().substring(1);

        
        Application.imageLoader.DisplayImage(item.icone, holder.deviceIcone);

        
        holder.deviceName.setChecked(item.isSelected());
        holder.device=item;
        return row;
    }

    public static class DeviceDisplayHolder {
        public ImageView deviceIcone;
        public CheckedTextView deviceName;
        public UpnpDevice device;
        // TextView deviceHydrate;
    }
}
