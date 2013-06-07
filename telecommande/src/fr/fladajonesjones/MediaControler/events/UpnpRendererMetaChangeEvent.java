package fr.fladajonesjones.MediaControler.events;

import fr.fladajonesjones.MediaControler.upnp.UpnpRendererDevice;

public class UpnpRendererMetaChangeEvent {
    public UpnpRendererDevice renderer;

    public UpnpRendererMetaChangeEvent(UpnpRendererDevice renderer) {

        this.renderer = renderer;
    }

}
