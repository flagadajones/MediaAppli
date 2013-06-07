package fr.flagadajones.mediarenderer.events.frommediaservice;

import fr.fladajonesjones.media.model.Piste;

import java.util.List;

public class PlayerChangeSongEvent {
    public Piste audioItem;
    public List<Piste> playlist;

    public PlayerChangeSongEvent(Piste audioItem, List<Piste> playlist) {
        this.audioItem = audioItem;
        this.playlist = playlist;
    }
}
