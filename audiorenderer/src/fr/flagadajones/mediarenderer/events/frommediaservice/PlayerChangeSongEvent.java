package fr.flagadajones.mediarenderer.events.frommediaservice;

import fr.fladajonesjones.media.model.Piste;

import java.util.List;

public class PlayerChangeSongEvent {
    public Piste audioItem;
    public int trackPosition;
    public List<Piste> playlist;

    public PlayerChangeSongEvent(Piste audioItem, List<Piste> playlist,int trackPosition) {
        this.audioItem = audioItem;
        this.playlist = playlist;
        this.trackPosition=trackPosition;
    }
}
