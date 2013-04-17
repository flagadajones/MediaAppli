package fr.flagadajones.mediarenderer.events.fromupnpservice;

import fr.fladajonesjones.media.model.Piste;

public class PlayerInitializeEvent {
	public int pos = -1;
	public Piste item;

	public PlayerInitializeEvent(int pos) {
		this.pos = pos;
	}

	public PlayerInitializeEvent(Piste url) {
		this.item = url;
	}
}
