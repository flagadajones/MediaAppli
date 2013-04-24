package fr.flagadajones.mediarenderer.events.frommediaservice;

import java.util.List;

import fr.fladajonesjones.media.model.Piste;

public class PlayerChangeSongEvent {
    public Piste audioItem;
    public List<Piste> playlist;

    public PlayerChangeSongEvent(Piste audioItem, List<Piste> playlist) {
        this.audioItem = audioItem;
        this.playlist = playlist;
    }
}
