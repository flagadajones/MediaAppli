/**
 * 
 */

package fr.fladajonesjones.MediaControler.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.squareup.otto.Subscribe;

import fr.fladajonesjones.MediaControler.Application;
import fr.fladajonesjones.MediaControler.R;
import fr.fladajonesjones.MediaControler.adapter.PisteRawAdapter;
import fr.fladajonesjones.MediaControler.events.UpnpRendererMetaChangeEvent;
import fr.fladajonesjones.MediaControler.upnp.UpnpRendererDevice;
import fr.fladajonesjones.media.model.Album;
import fr.flagadajones.media.util.BusManager;
import fr.flagadajones.media.util.StringUtils;
import fr.flagadajones.widget.holocircleseekbar.HoloCircleSeekBar;

/**
 * @author Andrew Neal
 */
public class NowPlayingFragment extends Fragment {

    public UpnpRendererDevice renderer;

    // Track, album, and artist name
    private TextView mTrackName, mAlbumArtistName;

    // Total and current time
    private TextView mTotalTime, mCurrentTime;

    // Album art
    private ImageView mAlbumArt;

    // Controls
    private ImageView mPlay;

    private Button mPrev, mNext;

    private HoloCircleSeekBar mSeekbar;
    private ListView listePiste;
    private PisteRawAdapter adapter = null;
    // Progress
    private SeekBar mProgress;

    // Where we are in the track
    private long mDuration, mLastSeekEventTime, mPosOverride = -1, mStartSeekPos = 0;

    private boolean mFromTouch, paused = false;

    // Handler
    private static final int REFRESH = 1;// , UPDATEINFO = 2;

    // Notify if repeat or shuffle changes
    private Toast mToast;

    public NowPlayingFragment() {
        super();

    }

    // public NowPlayingFragment(UpnpRendererDevice renderer) {
    // super();
    // this.renderer = renderer;
    // }

    @Override
    public void onResume() {
        super.onResume();
        BusManager.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusManager.getInstance().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.renderer_now_playing, container, false);

        mTrackName = (TextView) root.findViewById(R.id.SongName);

        mAlbumArtistName = (TextView) root.findViewById(R.id.ArtisteName);

        mAlbumArt = (ImageView) root.findViewById(R.id.AlbumArt);

        listePiste = (ListView) root.findViewById(R.id.pisteListe);

        adapter = new PisteRawAdapter(this.getActivity(),renderer);
        listePiste.setAdapter(adapter);
        mPrev = (Button) root.findViewById(R.id.ButtonPrev);
        mPrev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                renderer.seekPiste(renderer.positionInfo.getTrack().getValue().intValue()-1);
            }
        });
        mNext = (Button) root.findViewById(R.id.ButtonNext);
        mNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                renderer.seekPiste(renderer.positionInfo.getTrack().getValue().intValue()+1);
            }
        });


                                mSeekbar=(HoloCircleSeekBar) root.findViewById(R.id.Position);

        mSeekbar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(renderer.playing)
                renderer.pause();
                else
                    renderer.play();
            }
        });



        updateUI();
        return root;
    }

    @Subscribe
    public void onMetaChange(UpnpRendererMetaChangeEvent event) {
                      if (event.renderer==this.renderer){


                              getActivity().runOnUiThread(new Runnable() {
                                  @Override
                                  public void run() {
                                      updateUI();
                                  }
                              });

                          }
                      }


    public void updateUI(){

        mTrackName.setText(renderer.getMusique().titre);
        //mAlbumArtistNamerenderer=renderer.getMusique().titre;
        Application.imageLoader.DisplayImage(renderer.getMusique().albumArt, mAlbumArt);

        if(renderer.positionInfo!=null){
            mSeekbar.setMax(new Long(renderer.positionInfo.getTrackDurationSeconds()).intValue());
            mSeekbar.setProgress(renderer.positionInfo.getRelCount());
        }
        else{
            mSeekbar.setMax(100);
            mSeekbar.setProgress(0);

        }

        adapter.clear();
        adapter.addAll(((Album)renderer.getMusique()).getPistes());
        listePiste.setSelection(renderer.positionInfo.getTrack().getValue().intValue());
        listePiste.setSelected(true);
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        paused = true;
    }

}
