package fr.flagadajones.mediarenderer.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import fr.flagadajones.media.util.BusManager;
import fr.flagadajones.mediarenderer.Application;
import fr.flagadajones.mediarenderer.R;
import fr.flagadajones.mediarenderer.events.PlayerChangeSongEvent;
import fr.flagadajones.mediarenderer.events.PlayerErrorEvent;
import fr.flagadajones.mediarenderer.events.PlayerInitializeSuccess;
import fr.flagadajones.mediarenderer.events.PlayerPauseEvent;
import fr.flagadajones.mediarenderer.events.PlayerStartEvent;
import fr.flagadajones.mediarenderer.events.PlayerStopEvent;
import fr.flagadajones.mediarenderer.events.PlayerUpdatePosEvent;
import fr.flagadajones.mediarenderer.services.MediaPlayerService;

public class MainActivity extends Activity {

    private ImageView albumArt;
    private Button buttonPlayPause;
    private SeekBar seekBar;
    private TextView songName;
    private TextView artisteName;
    private ListView pisteListe;
    PisteRawAdapter adapter = new PisteRawAdapter(this);
    private boolean mBound = false;

    @Override
    protected void onResume() {
        super.onResume();
        BusManager.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusManager.getInstance().unregister(this);
    }

    private ServiceConnection mediaPlayerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder serviceBinder) {
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    private void initViews() {
        buttonPlayPause = (Button) findViewById(R.id.ButtonPlayStop);
        albumArt = (ImageView) findViewById(R.id.AlbumArt);
        seekBar = (SeekBar) findViewById(R.id.Position);
        artisteName = (TextView) findViewById(R.id.ArtisteName);
        songName = (TextView) findViewById(R.id.SongName);
        pisteListe = (ListView) findViewById(R.id.Piste);
        pisteListe.setAdapter(adapter);

    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        // Activate StrictMode
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
            .detectAll().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll()
            .penaltyLog().build());

        setContentView(R.layout.main);
        initViews();
        bindToService();
    }

    private boolean mediaPlayerServiceRunning() {

        ActivityManager manager = (ActivityManager) Application.instance.getSystemService(ACTIVITY_SERVICE);

        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("fr.flagadajones.mediarenderer.services.MediaPlayerService".equals(service.service.getClassName())) {
                return true;
            }
        }

        return false;
    }

    public void bindToService() {
        Intent intent = new Intent(this, MediaPlayerService.class);

        if (mediaPlayerServiceRunning()) {
            // Bind to LocalService
            bindService(intent, mediaPlayerServiceConnection, Context.BIND_AUTO_CREATE);
        } else {
            startService(intent);
            bindService(intent, mediaPlayerServiceConnection, Context.BIND_AUTO_CREATE);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Subscribe
    public void onInitializePlayerSuccess(final PlayerInitializeSuccess event) {
        runOnUiThread(new Runnable() {
            public void run() {

                seekBar.setMax(event.duree);
            }
        });
    }

   
    @Subscribe
    public void onErrorPlayer(final PlayerErrorEvent event) {
    }

    @Subscribe
    public void onPausePlayer(final PlayerPauseEvent event) {
        runOnUiThread(new Runnable() {
            public void run() {
                buttonPlayPause.setText("PAUSE");
            }
        });

    }

    @Subscribe
    public void onStartPlayer(final PlayerStartEvent event) {

        runOnUiThread(new Runnable() {
            public void run() {
                buttonPlayPause.setText("PLAY");
            }
        });
    }

    @Subscribe
    public void onStopPlayer(final PlayerStopEvent event) {

        runOnUiThread(new Runnable() {
            public void run() {
                buttonPlayPause.setText("STOP");
            }
        });

    }

    @Subscribe
    public void onUpdatePosPlayer(final PlayerUpdatePosEvent event) {

        runOnUiThread(new Runnable() {
            public void run() {
                seekBar.setProgress(event.pos);
            }
        });
    }

    @Subscribe
    public void onChangeSong(final PlayerChangeSongEvent event) {
        runOnUiThread(new Runnable() {
            public void run() {
                Application.imageLoader.DisplayImage(event.audioItem.albumArt, albumArt);
                songName.setText(event.audioItem.titre);
                artisteName.setText(event.audioItem.artiste);
                adapter.clear();
                adapter.addAll(event.playlist);
            }
        });
    }

    private static int CODE_RETOUR = 1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menuprincipal, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.itemOptions) {

            startActivityForResult(new Intent(this, MesPreferences.class), CODE_RETOUR);

        }

        return super.onOptionsItemSelected(item);

    }
}
