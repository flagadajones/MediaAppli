package fr.flagadajones.widget.testseekbar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.testseekbar.R;
import com.fima.cardsui.objects.Card;

public class PisteCard extends Card {

    // public PlayerCard(String titlePlay, String description, String color,
    // String titleColor, Boolean hasOverflow, Boolean isClickable) {
    // super(titlePlay, description, color, titleColor, hasOverflow,
    // isClickable);
    // }

    public PisteCard(String titre,String duree) {
        super(titre,duree);
    }

    boolean changedPosition = false;
    boolean play = false;

    @Override
    public View getCardContent(final Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.piste_row, null);

            ((TextView) v.findViewById(R.id.pisteTitre)).setText(title);
            ((TextView) v.findViewById(R.id.pisteDuree)).setText(desc);

            return v;
        
    }

}
