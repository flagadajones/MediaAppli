package fr.fladajonesjones.MediaControler.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import fr.fladajonesjones.MediaControler.R;
import fr.fladajonesjones.MediaControler.events.NowPlayingSeekEvent;
import fr.fladajonesjones.MediaControler.upnp.UpnpRendererDevice;
import fr.fladajonesjones.media.model.Piste;
import fr.flagadajones.media.util.BusManager;

public class PisteRawAdapter extends ArrayAdapter<Piste> {

    private LayoutInflater inflater;
    private UpnpRendererDevice renderer;

    public PisteRawAdapter(Context context, UpnpRendererDevice renderer) {
        super(context, -1);
        this.renderer = renderer;

        inflater = ((Activity) context).getLayoutInflater();

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        PisteRowHolder holder;

        if (row == null) {
            row = inflater.inflate(R.layout.piste_row, null, false);

            holder = new PisteRowHolder();
            holder.pisteTitre = (TextView) row.findViewById(R.id.pisteTitre);
            holder.pisteDuree = (TextView) row.findViewById(R.id.pisteDuree);

            row.setTag(holder);
        } else {
            holder = (PisteRowHolder) row.getTag();
        }
        Piste item = getItem(position);
        if (renderer.positionInfo != null && position == renderer.positionInfo.getTrack().getValue())
            row.setBackgroundColor(getContext().getResources().getColor(android.R.color.holo_green_light));
        else
            row.setBackgroundColor(0);

        holder.pisteTitre.setText(item.titre);
        holder.pisteDuree.setText(item.duree);

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BusManager.getInstance().post(new NowPlayingSeekEvent(NowPlayingSeekEvent.TRACK_NB, position));
            }
        });

        return row;
    }

    public static class PisteRowHolder {
        public TextView pisteTitre;
        public TextView pisteDuree;

    }
}
