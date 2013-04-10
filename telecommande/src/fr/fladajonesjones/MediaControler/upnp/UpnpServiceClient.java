package fr.fladajonesjones.MediaControler.upnp;

public interface UpnpServiceClient {

 public void onRendererDeviceAdded(UpnpRendererDevice renderer);
    
    public void onRendererDeviceRemoved(UpnpRendererDevice renderer);
    
public void onServerDeviceAdded(UpnpServerDevice server);
    
    public void onServerDeviceRemoved(UpnpServerDevice server);
}
