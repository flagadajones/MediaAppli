package fr.flagadajones.mediarenderer.events;

import java.util.List;

import fr.flagadajones.mediarenderer.AudioItem;

public class PlayerPlayListEvent {

	public List<AudioItem> list;
	
	public PlayerPlayListEvent(List<AudioItem> list) {
		this.list=list;
	}
}
