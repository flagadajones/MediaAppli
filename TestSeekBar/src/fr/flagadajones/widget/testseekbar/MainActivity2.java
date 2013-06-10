package fr.flagadajones.widget.testseekbar;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import com.example.testseekbar.R;
import fr.flagadajones.widget.holocircleseekbar.HoloCircleSeekBar;

public class MainActivity2 extends Activity {

    boolean changedPosition;
    boolean play = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        final HoloCircleSeekBar principal = (HoloCircleSeekBar) findViewById(R.id.Position);

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
                            principal.setThumb(MainActivity2.this.getResources().getDrawable(R.drawable.playerpause)); // do
                            // action
                            // here
                        else
                            principal.setThumb(MainActivity2.this.getResources().getDrawable(R.drawable.playerplay)); // do
                        play = !play;
                    }
                    changedPosition = false;
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
