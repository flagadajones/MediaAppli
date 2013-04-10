package fr.flagadajones.mediarenderer.events;

import java.util.List;

import fr.flagadajones.mediarenderer.AudioItem;

public class PlayerChangeSongEvent {
    public AudioItem audioItem;
    public List<AudioItem> playlist;

    public PlayerChangeSongEvent(AudioItem audioItem, List<AudioItem> playlist) {
        this.audioItem = audioItem;
        this.playlist = playlist;
    }
}
