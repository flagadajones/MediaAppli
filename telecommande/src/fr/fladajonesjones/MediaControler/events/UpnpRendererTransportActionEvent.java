package fr.fladajonesjones.MediaControler.events;

import org.fourthline.cling.support.model.TransportAction;

public class UpnpRendererTransportActionEvent {
public TransportAction[] actions;

public UpnpRendererTransportActionEvent(TransportAction[] actions) {
    super();
    this.actions = actions;
}

}
