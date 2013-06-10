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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import fr.fladajonesjones.media.model.Album;
import fr.flagadajones.media.util.BusManager;
import fr.flagadajones.media.util.StringUtils;
import fr.flagadajones.mediarenderer.Application;
import fr.flagadajones.mediarenderer.R;
import fr.flagadajones.mediarenderer.events.frommediaservice.PlayerChangeSongEvent;
import fr.flagadajones.mediarenderer.events.frommediaservice.PlayerErrorEvent;
import fr.flagadajones.mediarenderer.events.frommediaservice.PlayerUpdatePosEvent;
import fr.flagadajones.mediarenderer.events.fromupnpservice.PlayerPauseEvent;
import fr.flagadajones.mediarenderer.events.fromupnpservice.PlayerStartEvent;
import fr.flagadajones.mediarenderer.events.fromupnpservice.PlayerStopEvent;
import fr.flagadajones.mediarenderer.events.fromupnpservice.UpnpRendererTransportActionEvent;
import fr.flagadajones.mediarenderer.services.MediaPlayerService;
import fr.flagadajones.widget.holocircleseekbar.HoloCircleSeekBar;
import org.fourthline.cling.support.model.TransportAction;

public class MainActivity extends Activity {

    private static int CODE_RETOUR = 1;
    // Track, album, and artist name
    private TextView mTrackName, mAlbumArtistName;
    // Album art
    private ImageView mAlbumArt;

    // Controls
    private Button mPrev, mNext;
    private Button mPlay, mPause, mStop;
    private HoloCircleSeekBar mSeekbar;
    private ListView listePiste;
    private PisteRawAdapter adapter = null;
    private boolean mBound = false;

    private MediaPlayerService mediaPlayerService;

    private ServiceConnection mediaPlayerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder serviceBinder) {
            mBound = true;
            mediaPlayerService=(MediaPlayerService )serviceBinder;

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

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

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        bindToService();

    }

    private void initViews() {
        mTrackName = (TextView) findViewById(R.id.SongName);

        mAlbumArtistName = (TextView) findViewById(R.id.ArtisteName);

        mAlbumArt = (ImageView) findViewById(R.id.AlbumArt);

        listePiste = (ListView) findViewById(R.id.pisteListe);

        adapter = new PisteRawAdapter(MainActivity.this);
        listePiste.setAdapter(adapter);


        mPrev = (Button) findViewById(R.id.ButtonPrev);
/*        mPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                renderer.seekPiste(renderer.positionInfo.getTrack().getValue().intValue() - 1);
            }
        });
*/
        mNext = (Button) findViewById(R.id.ButtonNext);
/*        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                renderer.seekPiste(renderer.positionInfo.getTrack().getValue().intValue() + 1);
            }
        });
*/        mPlay = (Button) findViewById(R.id.ButtonPlay);
/*        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                renderer.play();
            }
        });
*/        mPause = (Button)findViewById(R.id.ButtonPause);
/*        mPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                renderer.pause();
            }
        });
*/        mStop = (Button) findViewById(R.id.ButtonStop);
/*        mStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                renderer.stop();

            }
        });
*/
        mSeekbar = (HoloCircleSeekBar) findViewById(R.id.Position);

/*        mSeekbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (renderer.playing)
                    renderer.pause();
                else
                    renderer.play();
            }
        });
*/

    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        // Activate StrictMode
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll()
                .penaltyLog().build());

        setContentView(R.layout.renderer_now_playing);
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
    public void onErrorPlayer(final PlayerErrorEvent event) {
    }

    @Subscribe
    public void onPausePlayer(final PlayerPauseEvent event) {
        runOnUiThread(new Runnable() {
            public void run() {
               // buttonPlayPause.setText("PAUSE");
            }
        });

    }

    @Subscribe
    public void onStartPlayer(final PlayerStartEvent event) {

        runOnUiThread(new Runnable() {
            public void run() {
                //         buttonPlayPause.setText("PLAY");
            }
        });
    }

    @Subscribe
    public void onStopPlayer(final PlayerStopEvent event) {

        runOnUiThread(new Runnable() {
            public void run() {
                //buttonPlayPause.setText("STOP");
            }
        });

    }

    @Subscribe
    public void onUpdatePosPlayer(final PlayerUpdatePosEvent event) {

        runOnUiThread(new Runnable() {
            public void run() {
                mSeekbar.setProgress(event.pos/1000);
            }
        });
    }

    @Subscribe
    public void onChangeSong(final PlayerChangeSongEvent event) {
        runOnUiThread(new Runnable() {
            public void run() {
                if(event.audioItem==null)
                    return;
                if (event.audioItem.albumArt != null)
                    Picasso.with(MainActivity.this).load(event.audioItem.albumArt).placeholder(R.drawable.stub)
                        .error(R.drawable.stub).into(mAlbumArt);
                //Application.imageLoader.DisplayImage(event.audioItem.albumArt, albumArt);
                mTrackName.setText(event.audioItem.titre);
                mAlbumArtistName.setText(event.audioItem.artiste);
                mSeekbar.setMax(StringUtils.makeLongFromStringTime(event.audioItem.duree).intValue());
                mSeekbar.setProgress(0);
                adapter.clear();
                if (event.playlist.size()!=1) {
                    if(adapter.trackPosition==0)
                        adapter.addAll(event.playlist);
                        adapter.trackPosition=event.trackPosition;


                    listePiste.setSelection(event.trackPosition);
                    listePiste.setSelected(true);
                    listePiste.setVisibility(View.VISIBLE);
                } else {
                    listePiste.setVisibility(View.INVISIBLE);
                }
            }



        });
    }

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

    @Subscribe
    public void onUpnpRendererTransportActionEvent(final UpnpRendererTransportActionEvent event) {
        if (event.actions == null)
            return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                desactiveButton(mPlay);
                desactiveButton(mNext);
                desactiveButton(mPause);
                desactiveButton(mPrev);
                desactiveButton(mStop);

                for (TransportAction action : event.actions) {
                    switch (action) {
                        case Play:
                            activeButton(mPlay);
                            break;
                        case Next:
                            activeButton(mNext);
                            break;
                        case Pause:
                            activeButton(mPause);
                            break;
                        case Previous:
                            activeButton(mPrev);
                            break;
                        case Stop:
                            activeButton(mStop);
                            break;
                        default:
                            break;
                    }

                }
            }
        });
    }

    private void activeButton(Button button) {
        button.setEnabled(true);
        button.setAlpha(1);
    }

    private void desactiveButton(Button button) {
        button.setEnabled(false);
        button.setAlpha(0.2f);
    }



}
