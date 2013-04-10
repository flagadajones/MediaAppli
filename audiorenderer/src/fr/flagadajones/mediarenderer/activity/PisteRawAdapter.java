package fr.flagadajones.mediarenderer.activity;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import fr.flagadajones.mediarenderer.AudioItem;
import fr.flagadajones.mediarenderer.R;

public class PisteRawAdapter extends ArrayAdapter<AudioItem> {

    private LayoutInflater inflater;

    public PisteRawAdapter(Context context) {
        super(context, -1);

        inflater = ((Activity) context).getLayoutInflater();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        PisteRowHolder holder = null;

        if (row == null) {
            row = inflater.inflate(R.layout.piste_row, null, false);

            holder = new PisteRowHolder();
            holder.pisteTitre = (TextView) row.findViewById(R.id.pisteTitre);
            holder.pisteDuree = (TextView) row.findViewById(R.id.pisteDuree);

            row.setTag(holder);
        } else {
            holder = (PisteRowHolder) row.getTag();
        }
        AudioItem item = getItem(position);

        holder.pisteTitre.setText(item.title);
        holder.pisteDuree.setText(item.duration);
        return row;
    }

    public static class PisteRowHolder {
        public TextView pisteTitre;
        public TextView pisteDuree;

    }
}
