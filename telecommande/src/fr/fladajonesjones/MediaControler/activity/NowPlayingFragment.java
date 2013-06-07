
package fr.fladajonesjones.MediaControler.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import fr.fladajonesjones.MediaControler.R;
import fr.fladajonesjones.MediaControler.adapter.PisteRawAdapter;
import fr.fladajonesjones.MediaControler.events.NowPlayingSeekEvent;
import fr.fladajonesjones.MediaControler.events.UpnpRendererMetaChangeEvent;
import fr.fladajonesjones.MediaControler.upnp.UpnpRendererDevice;
import fr.fladajonesjones.media.model.Album;
import fr.flagadajones.media.util.BusManager;
import fr.flagadajones.widget.holocircleseekbar.HoloCircleSeekBar;

public class NowPlayingFragment extends Fragment {

    public UpnpRendererDevice renderer;
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

    public NowPlayingFragment() {
        super();

    }

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

        adapter = new PisteRawAdapter(this.getActivity(), renderer);
        listePiste.setAdapter(adapter);
        mPrev = (Button) root.findViewById(R.id.ButtonPrev);
        mPrev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                renderer.seekPiste(renderer.positionInfo.getTrack().getValue().intValue() - 1);
            }
        });


        mNext = (Button) root.findViewById(R.id.ButtonNext);
        mNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                renderer.seekPiste(renderer.positionInfo.getTrack().getValue().intValue() + 1);
            }
        });
        mPlay = (Button) root.findViewById(R.id.ButtonPlay);
        mPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                renderer.play();
            }
        });
        mPause = (Button) root.findViewById(R.id.ButtonPause);
        mPause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                renderer.pause();
            }
        });
        mStop = (Button) root.findViewById(R.id.ButtonStop);
        mStop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                renderer.stop();

            }
        });

        mSeekbar = (HoloCircleSeekBar) root.findViewById(R.id.Position);

        mSeekbar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (renderer.playing)
                    renderer.pause();
                else
                    renderer.play();
            }
        });

        updateUI();
        return root;
    }

    @Subscribe
    public void onNowPlayingSeekEvent(NowPlayingSeekEvent event) {
        if (NowPlayingSeekEvent.TRACK_NB == event.seekType) {
            renderer.seekPiste(event.pos);
        }
    }

    @Subscribe
    public void onMetaChange(UpnpRendererMetaChangeEvent event) {
        if (event.renderer == this.renderer) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateUI();
                }
            });

        }
    }

    public void updateUI() {

        mTrackName.setText(renderer.getMusique().titre);
        // mAlbumArtistNamerenderer=renderer.getMusique().titre;
        Picasso.with(getActivity()).load(renderer.getMusique().albumArt).placeholder(R.drawable.stub)
                .error(R.drawable.bg_img_notfound).into(mAlbumArt);

        // Application.imageLoader.DisplayImage(renderer.getMusique().albumArt, mAlbumArt);

        if (renderer.positionInfo != null) {
            mSeekbar.setMax(Long.valueOf(renderer.positionInfo.getTrackDurationSeconds()).intValue());
            mSeekbar.setProgress(renderer.positionInfo.getRelCount());
        } else {
            mSeekbar.setMax(100);
            mSeekbar.setProgress(0);

        }

        adapter.clear();
        if (renderer.getMusique() instanceof Album) {
            adapter.addAll(((Album) renderer.getMusique()).getPistes());
            listePiste.setSelection(renderer.positionInfo.getTrack().getValue().intValue());
            listePiste.setSelected(true);
            listePiste.setVisibility(View.VISIBLE);
        } else {
            listePiste.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
