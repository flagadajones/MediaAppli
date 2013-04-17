package fr.flagadajones.mediarenderer.events.fromupnpservice;

public class PlayerSeekEvent {
	public int msec;

	public PlayerSeekEvent(int msec) {
		this.msec = msec;
	}
}
