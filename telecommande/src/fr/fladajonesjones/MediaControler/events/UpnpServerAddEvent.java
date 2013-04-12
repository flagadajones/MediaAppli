package fr.fladajonesjones.MediaControler.events;

import fr.fladajonesjones.MediaControler.upnp.UpnpServerDevice;

public class UpnpServerAddEvent extends UpnpServerEvent{
    public UpnpServerDevice device;

    public UpnpServerAddEvent(UpnpServerDevice d) {
        this.device = d;
    }

}
