package fr.flagadajones.mediarenderer.events.fromupnpservice;

public class PlayerSeekEvent {
    public int pos;

    public int seekType;

    public PlayerSeekEvent(int seekType, int pos) {
        this.pos = pos;
        this.seekType = seekType;
    }
}
