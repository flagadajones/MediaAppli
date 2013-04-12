package fr.fladajonesjones.MediaControler.events;

import fr.fladajonesjones.MediaControler.upnp.UpnpRendererDevice;

public class UpnpRendererRemoveEvent {
    public UpnpRendererDevice device;

    public UpnpRendererRemoveEvent(UpnpRendererDevice renderer) {
        this.device = renderer;
    }

}
