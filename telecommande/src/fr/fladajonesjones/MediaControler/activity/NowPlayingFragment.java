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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import fr.fladajonesjones.MediaControler.Application;
import fr.fladajonesjones.MediaControler.R;
import fr.fladajonesjones.MediaControler.events.UpnpRendererMetaChangeEvent;
import fr.fladajonesjones.MediaControler.upnp.UpnpRendererDevice;
import fr.flagadajones.media.util.BusManager;
import fr.flagadajones.media.util.StringUtils;

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
    private ImageButton mPlay;

    private ImageButton mPrev, mNext;

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

        mPrev = (ImageButton) root.findViewById(R.id.ButtonPrev);
        mNext = (ImageButton) root.findViewById(R.id.ButtonNext);

        return root;
    }

    @Subscribe
    public void onMetaChange(UpnpRendererMetaChangeEvent event) {
        updateMusicInfo();
        setPauseButtonImage();
    }

    @Override
    public void onStart() {
        super.onStart();
        long next = refreshNow();
        queueNextRefresh(next);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        paused = true;
    }

    /**
     * Play and pause music
     */
    private void doPauseResume() {
        // try {
        // if (MusicUtils.mService != null) {
        // if (MusicUtils.mService.isPlaying()) {
        // MusicUtils.mService.pause();
        // } else {
        // MusicUtils.mService.play();
        // }
        // }
        // refreshNow();
        // setPauseButtonImage();
        // } catch (RemoteException ex) {
        // ex.printStackTrace();
        // }
    }

    /**
     * Set the play and pause image
     */
    private void setPauseButtonImage() {
        if (renderer.isPlaying()) {
            mPlay.setImageResource(R.drawable.apollo_holo_light_pause);
        } else {
            mPlay.setImageResource(R.drawable.apollo_holo_light_play);
        }
    }

    /**
     * @param delay
     */
    private void queueNextRefresh(long delay) {
        if (!paused) {
            Message msg = mHandler.obtainMessage(REFRESH);
            mHandler.removeMessages(REFRESH);
            mHandler.sendMessageDelayed(msg, delay);
        }
    }

    // FIXME remove REFRESH Handler;
    /**
     * We need to refresh the time via a Handler
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case REFRESH:
                long next = refreshNow();
                queueNextRefresh(next);
                break;
            default:
                break;
            }
        }
    };

    /**
     * Drag to a specfic duration
     */
    private final OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
        @Override
        public void onStartTrackingTouch(SeekBar bar) {
            mLastSeekEventTime = 0;
            mFromTouch = true;
        }

        @Override
        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (!fromuser)
                return;
            long now = SystemClock.elapsedRealtime();
            if ((now - mLastSeekEventTime) > 250) {
                mLastSeekEventTime = now;
                mPosOverride = mDuration * progress / 1000;

                renderer.seekTo(mPosOverride);

                if (!mFromTouch) {
                    refreshNow();
                    mPosOverride = -1;
                }
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar bar) {
            mPosOverride = -1;
            mFromTouch = false;
        }
    };

    /**
     * @return current time
     */
    private long refreshNow() {
        if (renderer == null)
            return 500;

        long pos = mPosOverride < 0 ? renderer.getPosition() : mPosOverride;
        long remaining = 1000 - (pos % 1000);
        if ((pos >= 0) && (mDuration > 0)) {
            mCurrentTime.setText(StringUtils.makeTimeString(pos / 1000));

            if (renderer.isPlaying()) {
                mCurrentTime.setVisibility(View.VISIBLE);
                mCurrentTime.setTextColor(getResources().getColor(R.color.transparent_black));
                // Theme chooser
            } else {
                // blink the counter
                int col = mCurrentTime.getCurrentTextColor();
                mCurrentTime.setTextColor(col == getResources().getColor(R.color.transparent_black) ? getResources()
                        .getColor(R.color.holo_blue_dark) : getResources().getColor(R.color.transparent_black));
                remaining = 500;
            }

            mProgress.setProgress((int) (1000 * pos / mDuration));
        } else {
            mCurrentTime.setText("--:--");
            mProgress.setProgress(1000);
        }
        return remaining;

    }

    /**
     * Update what's playing
     */
    private void updateMusicInfo() {
        if (renderer == null) {
            return;
        }

        // FIXME
        String artistName = "";// renderer.getMusique().artistName;
        String albumName = "";// renderer.getMusique().albumName;
        String trackName = renderer.getMusique().titre;
        mTrackName.setText(trackName);
        mAlbumArtistName.setText(albumName + " - " + artistName);
        // mDuration = renderer.getMusique().durgetDuree();
        // mTotalTime.setText(StringUtils.makeTimeString(mDuration / 1000));

        mTotalTime.setText(renderer.getMusique().getDuree());
        mDuration = StringUtils.makeLongFromStringTime(renderer.getMusique().getDuree());

        Application.imageLoader.DisplayImage(renderer.getMusique().albumArt, mAlbumArt);

    }

    /**
     * Takes you into the @TracksBrowser to view all of the tracks on the current album
     */
    private void tracksBrowser() {
        // permet d'afficher la liste des piste d'un album
        /*
         * String artistName = MusicUtils.getArtistName(); String albumName = MusicUtils.getAlbumName(); long id =
         * MusicUtils.getCurrentAlbumId(); Bundle bundle = new Bundle(); bundle.putString(MIME_TYPE,
         * Audio.Albums.CONTENT_TYPE); bundle.putString(ARTIST_KEY, artistName); bundle.putString(ALBUM_KEY, albumName);
         * bundle.putLong(BaseColumns._ID, id); Intent intent = new Intent(Intent.ACTION_VIEW);
         * intent.setClass(getActivity(), TracksBrowser.class); intent.putExtras(bundle);
         * getActivity().startActivity(intent);
         */
    }

    /**
     * Takes you into the @TracksBrowser to view all of the tracks and albums by the current artist
     */
    private void tracksBrowserArtist() {
        // peremt d'afficher toutes les chansons d'un artiste
        /*
         * String artistName = MusicUtils.getArtistName(); long id = MusicUtils.getCurrentArtistId(); Bundle bundle =
         * new Bundle(); bundle.putString(MIME_TYPE, Audio.Artists.CONTENT_TYPE); bundle.putString(ARTIST_KEY,
         * artistName); bundle.putLong(BaseColumns._ID, id); ApolloUtils.setArtistId(artistName, id, ARTIST_ID,
         * getActivity()); Intent intent = new Intent(Intent.ACTION_VIEW); intent.setClass(getActivity(),
         * TracksBrowser.class); intent.putExtras(bundle); getActivity().startActivity(intent);
         */
    }
}
