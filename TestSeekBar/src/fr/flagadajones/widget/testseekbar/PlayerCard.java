package fr.flagadajones.widget.testseekbar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.example.testseekbar.R;
import com.fima.cardsui.objects.Card;

import fr.flagadajones.widget.holocircleseekbar.HoloCircleSeekBar;

public class PlayerCard extends Card {


    protected View mCardLayout;

    boolean changedPosition = false;
    boolean play = false;

   
    public View getCardContent(final Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.player_card, null);

        final HoloCircleSeekBar principal = (HoloCircleSeekBar) v.findViewById(R.id.Position);

        principal.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    changedPosition = true;
                    // principal.setProgress(principal.getValue());
                    return false;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (!changedPosition) {
                        if (play)
                            principal.setThumb(context.getResources().getDrawable(R.drawable.playerpause)); // do
                                                                                                            // action
                                                                                                            // here
                        else
                            principal.setThumb(context.getResources().getDrawable(R.drawable.playerplay)); // do
                        play = !play;
                    }
                    changedPosition = false;
                }
                return true;
            }
        });
        return v;
    }

}
