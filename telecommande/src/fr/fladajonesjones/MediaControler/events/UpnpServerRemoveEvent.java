package fr.fladajonesjones.MediaControler.events;

import fr.fladajonesjones.MediaControler.upnp.UpnpServerDevice;

public class UpnpServerRemoveEvent extends UpnpServerEvent {
    public UpnpServerDevice device;

    public UpnpServerRemoveEvent(UpnpServerDevice serveur) {
        this.device = serveur;
    }

}
