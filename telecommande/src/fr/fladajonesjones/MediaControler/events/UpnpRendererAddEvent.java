package fr.fladajonesjones.MediaControler.events;

import fr.fladajonesjones.MediaControler.upnp.UpnpRendererDevice;

public class UpnpRendererAddEvent {
    public UpnpRendererDevice device;

    public UpnpRendererAddEvent(UpnpRendererDevice d) {
        this.device = d;
    }

}
