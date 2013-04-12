package fr.fladajonesjones.MediaControler.upnp;

import java.util.Comparator;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.RemoteDevice;

public class UpnpDevice {

    String udn = "";

    public String getUdn() {
        return udn;
    }

    public void setUdn(String udn) {
        this.udn = udn;
    }

    Device device;
    public String icone;
    boolean selected;

    public UpnpDevice() {

    }

    public UpnpDevice(Device device) {
        this.device = device;

        this.udn = device.getIdentity().getUdn().getIdentifierString();
        this.selected = false;
        setIcone();

    }

    private void setIcone() {

        if (device.isFullyHydrated()) {

            if (device.getIcons().length != 0) {
                icone = ((RemoteDevice) device).normalizeURI(device.getIcons()[0].getUri()).toString();

            }
        }

    }

    public void setDevice(Device device) {
        this.device = device;

        this.udn = device.getIdentity().getUdn().getIdentifierString();
        setIcone();
    }

    public String getName() {
        if (device != null)
            return device.getDetails().getFriendlyName();
        if (udn.equals(""))
            return "Introuvable";
        else
            return udn;

    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public UpnpDevice(Device device, boolean selected) {
        this.device = device;
        this.selected = selected;

    }

    public Device getDevice() {
        return device;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UpnpDevice that = (UpnpDevice) o;
        if (device != null && that.device != null)
            return device.equals(that.device);
        return udn.equals(that.udn);
    }

    @Override
    public int hashCode() {
        return device.hashCode();
    }

    @Override
    public String toString() {
        String name = device.getDetails() != null && device.getDetails().getFriendlyName() != null ? device
                .getDetails().getFriendlyName() : device.getDisplayString();
        // Display a little star while the device is being loaded (see
        // performance optimization earlier)
        return device.isFullyHydrated() ? name : name + " *";
    }

    static public final Comparator<UpnpDevice> DISPLAY_COMPARATOR = new Comparator<UpnpDevice>() {
        public int compare(UpnpDevice a, UpnpDevice b) {
            return a.udn.compareTo(b.udn);
            // return a.toString().compareTo(b.toString());
        }
    };
}
