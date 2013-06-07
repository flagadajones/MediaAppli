package fr.flagadajones.mediarenderer.events.fromupnpservice;

import fr.fladajonesjones.media.model.Piste;

import java.util.List;

public class PlayerPlayListEvent {

    public List<Piste> list;

    public PlayerPlayListEvent(List<Piste> list) {
        this.list = list;
    }
}
