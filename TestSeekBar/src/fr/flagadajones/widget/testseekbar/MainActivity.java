package fr.flagadajones.widget.testseekbar;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import com.example.testseekbar.R;
import fr.flagadajones.widget.holocircleseekbar.HoloCircleSeekBar;

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final HoloCircleSeekBar principal = (HoloCircleSeekBar) findViewById(R.id.picker);
        HoloCircleSeekBar rotation = (HoloCircleSeekBar) findViewById(R.id.rotation);
        rotation.setOnSeekBarChangeListener(new HoloCircleSeekBar.OnCircleSeekBarChangeListener() {

            @Override
            public void onProgressChanged(HoloCircleSeekBar arg0, int arg1, boolean arg2) {
                principal.setRotate(arg1);

            }
        });

        HoloCircleSeekBar angle = (HoloCircleSeekBar) findViewById(R.id.angle);
        angle.setOnSeekBarChangeListener(new HoloCircleSeekBar.OnCircleSeekBarChangeListener() {

            @Override
            public void onProgressChanged(HoloCircleSeekBar arg0, int arg1, boolean arg2) {
                principal.setMaxAngle(arg1);

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
