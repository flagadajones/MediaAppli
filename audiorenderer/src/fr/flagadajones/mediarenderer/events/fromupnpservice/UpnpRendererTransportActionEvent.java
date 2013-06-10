package fr.flagadajones.mediarenderer.events.fromupnpservice;


import org.fourthline.cling.support.model.TransportAction;

public class UpnpRendererTransportActionEvent {

    public TransportAction[] actions;

    public UpnpRendererTransportActionEvent(TransportAction[] actions) {
        this.actions = actions;
    }
}
