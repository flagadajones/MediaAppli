/**
 * 
 */

package fr.fladajonesjones.MediaControler.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import fr.fladajonesjones.MediaControler.Application;
import fr.fladajonesjones.MediaControler.R;
import fr.fladajonesjones.MediaControler.Util;
import fr.fladajonesjones.MediaControler.upnp.UpnpRendererDevice;

/**
 * @author Andrew Neal
 */
public class NowPlayingFragment extends Fragment {

    

    private UpnpRendererDevice renderer;

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
    private static final int REFRESH = 1, UPDATEINFO = 2;

    // Notify if repeat or shuffle changes
    private Toast mToast;

    public NowPlayingFragment(UpnpRendererDevice renderer) {
        super();
        this.renderer = renderer;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        
        View root = inflater.inflate(R.layout.device_now_playing, container, false);

        mTrackName = (TextView) root.findViewById(R.id.audio_player_track);
        mTrackName.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                tracksBrowser();
            }
        });
        mAlbumArtistName = (TextView) root.findViewById(R.id.audio_player_album_artist);
        mAlbumArtistName.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                tracksBrowserArtist();
            }
        });

        mTotalTime = (TextView) root.findViewById(R.id.audio_player_total_time);
        mCurrentTime = (TextView) root.findViewById(R.id.audio_player_current_time);

        mAlbumArt = (ImageView) root.findViewById(R.id.audio_player_album_art);

        mPrev = (ImageButton) root.findViewById(R.id.audio_player_prev);
        mPlay = (ImageButton) root.findViewById(R.id.audio_player_play);
        mNext = (ImageButton) root.findViewById(R.id.audio_player_next);

        // mPrev.setRepeatListener(mRewListener, 260);
        // mPrev.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // if (MusicUtils.mService == null)
        // return;
        // try {
        // if (MusicUtils.mService.position() < 2000) {
        // MusicUtils.mService.prev();
        // } else {
        // MusicUtils.mService.seek(0);
        // MusicUtils.mService.play();
        // }
        // } catch (RemoteException ex) {
        // ex.printStackTrace();
        // }
        // }
        // });

        mPlay.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                doPauseResume();
            }
        });

        // mNext.setRepeatListener(mFfwdListener, 260);
        // mNext.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // if (MusicUtils.mService == null)
        // return;
        // try {
        // MusicUtils.mService.next();
        // } catch (RemoteException ex) {
        // ex.printStackTrace();
        // }
        // }
        // });

        mProgress = (SeekBar) root.findViewById(android.R.id.progress);
        if (mProgress instanceof SeekBar) {
            SeekBar seeker = mProgress;
            seeker.setOnSeekBarChangeListener(mSeekListener);
        }
        mProgress.setMax(1000);

        return root;
    }

    /**
     * Update everything as the meta or playstate changes
     */
    private final BroadcastReceiver mStatusListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(UpnpRendererDevice.META_CHANGED))
                mHandler.sendMessage(mHandler.obtainMessage(UPDATEINFO));
            setPauseButtonImage();
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter f = new IntentFilter();
        f.addAction(UpnpRendererDevice.META_CHANGED);
        getActivity().registerReceiver(mStatusListener, new IntentFilter(f));

        long next = refreshNow();
        queueNextRefresh(next);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        paused = true;
        mHandler.removeMessages(REFRESH);
        getActivity().unregisterReceiver(mStatusListener);
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
            case UPDATEINFO:
                updateMusicInfo();
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
            mCurrentTime.setText(Util.makeTimeString(getActivity(), pos / 1000));

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

        //FIXME
        String artistName = "";//renderer.getMusique().artistName;
        String albumName = "";//renderer.getMusique().albumName;
        String trackName = renderer.getMusique().nom;
        mTrackName.setText(trackName);
        mAlbumArtistName.setText(albumName + " - " + artistName);
        mDuration = renderer.getMusique().getDuration();
        mTotalTime.setText(Util.makeTimeString(getActivity(), mDuration / 1000));


        Application.imageLoader.DisplayImage(renderer.getMusique().icone, mAlbumArt);

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
