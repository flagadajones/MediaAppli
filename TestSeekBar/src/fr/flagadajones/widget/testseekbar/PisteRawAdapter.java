package fr.flagadajones.widget.testseekbar;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.testseekbar.R;

public class PisteRawAdapter extends ArrayAdapter<Piste> {

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
        Piste item = getItem(position);

        holder.pisteTitre.setText(item.titre);
        holder.pisteDuree.setText(item.duree);
        return row;
    }

    public static class PisteRowHolder {
        public TextView pisteTitre;
        public TextView pisteDuree;

    }
}
