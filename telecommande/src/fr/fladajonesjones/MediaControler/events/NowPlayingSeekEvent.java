package fr.fladajonesjones.MediaControler.events;


public class NowPlayingSeekEvent {
    public static int TRACK_NB = 1;
    public int pos;

    public int seekType;

    public NowPlayingSeekEvent(int seekType, int pos) {
        this.pos = pos;
        this.seekType = seekType;
    }
}
