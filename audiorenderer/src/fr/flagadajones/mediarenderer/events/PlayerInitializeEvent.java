package fr.flagadajones.mediarenderer.events;

import fr.flagadajones.mediarenderer.AudioItem;

public class PlayerInitializeEvent {
	public int pos = -1;
	public AudioItem item;

	public PlayerInitializeEvent(int pos) {
		this.pos = pos;
	}

	public PlayerInitializeEvent(AudioItem url) {
		this.item = url;
	}
}
