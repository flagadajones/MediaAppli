package fr.flagadajones.mediarenderer.events;

import java.util.List;

import fr.fladajonesjones.media.model.Piste;

public class PlayerPlayListEvent {

	public List<Piste> list;
	
	public PlayerPlayListEvent(List<Piste> list) {
		this.list=list;
	}
}
