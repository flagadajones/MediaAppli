package fr.fladajonesjones.MediaControler.manager;

import java.util.ArrayList;
import java.util.List;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.model.message.header.UDADeviceTypeHeader;
import org.fourthline.cling.model.message.header.UDNHeader;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import fr.fladajonesjones.MediaControler.Application;
import fr.fladajonesjones.MediaControler.database.DeviceDAO;
import fr.fladajonesjones.MediaControler.events.UpnpRendererAddEvent;
import fr.fladajonesjones.MediaControler.events.UpnpRendererRemoveEvent;
import fr.fladajonesjones.MediaControler.events.UpnpServerAddEvent;
import fr.fladajonesjones.MediaControler.events.UpnpServerRemoveEvent;
import fr.fladajonesjones.MediaControler.upnp.UpnpDevice;
import fr.fladajonesjones.MediaControler.upnp.UpnpRendererDevice;
import fr.fladajonesjones.MediaControler.upnp.UpnpServerDevice;
import fr.fladajonesjones.MediaControler.upnp.UpnpService;
import fr.flagadajones.media.util.BusManager;

public class UpnpDeviceManager {
    private static String MEDIARENDERER_TYPE = "urn:schemas-upnp-org:device:MediaRenderer:1";
    private static String MEDIASERVER_TYPE = "urn:schemas-upnp-org:device:MediaServer:1";

    DeviceDAO deviceDAO = null;

    private static UpnpDeviceManager instance = null;
    public List<UpnpRendererDevice> lstRenderer = new ArrayList<UpnpRendererDevice>();
    public List<UpnpServerDevice> lstServer = new ArrayList<UpnpServerDevice>();

    static public AndroidUpnpService upnpService;
    private BrowseRegistryListener registryListener = new BrowseRegistryListener();

    private ServiceConnection serviceConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            upnpService = (AndroidUpnpService) service;
            upnpService.getRegistry().addListener(registryListener);
            if (UpnpDeviceManager.getInstance().libraryDevice != null)
                upnpService.getControlPoint().search(
                        new UDNHeader(new UDN(UpnpDeviceManager.getInstance().libraryDevice.getUdn())));
            if (UpnpDeviceManager.getInstance().rendererDevice != null)
                for (UpnpDevice device : UpnpDeviceManager.getInstance().rendererDevice) {
                    upnpService.getControlPoint().search(new UDNHeader(new UDN(device.getUdn())));
                }

            search();

        }

        public void onServiceDisconnected(ComponentName className) {
            upnpService = null;
        }
    };

    public static UpnpDeviceManager getInstance() {
        if (instance == null) {
            instance = new UpnpDeviceManager();
        }
        return instance;
    }

    public UpnpServerDevice libraryDevice = null;
    public List<UpnpRendererDevice> rendererDevice = new ArrayList<UpnpRendererDevice>();
    private UpnpRendererDevice defaultRenderer = null;

    private UpnpDeviceManager() {
        deviceDAO = new DeviceDAO();

        List<UpnpDevice> devices = deviceDAO.getAllDevice();

        for (UpnpDevice device : devices) {

            if (device instanceof UpnpRendererDevice) {
                rendererDevice.add((UpnpRendererDevice) device);
            } else if (device instanceof UpnpServerDevice) {
                libraryDevice = (UpnpServerDevice) device;
            }
            device.setSelected(true);
        }

        Application.instance.bindService(new Intent(Application.instance, UpnpService.class), serviceConnection,
                Context.BIND_AUTO_CREATE);

    }

    public UpnpRendererDevice getDefaultRenderer() {
        return defaultRenderer;
    }

    public void setDefaultRenderer(UpnpRendererDevice defaultRenderer) {
        this.defaultRenderer = defaultRenderer;
    }

    public void setLibraryDevice(UpnpServerDevice device) {
        
        if (libraryDevice != null)
            deviceDAO.removeDevice(libraryDevice.getUdn());
if(device!=null){
        deviceDAO.insertDevice(device);
}
        libraryDevice = device;

    }

    public UpnpServerDevice getLibraryDevice() {
        return libraryDevice;

    }

    public void addRendererDevice(UpnpRendererDevice device) {

        deviceDAO.insertDevice(device);

        rendererDevice.add(device);

    }

    public void removeRendererDevice(UpnpRendererDevice device) {

        deviceDAO.removeDevice(device.getUdn());

        rendererDevice.remove(device);

    }

    public void search() {
        if (upnpService == null)
            return;

        upnpService.getControlPoint().search(new UDADeviceTypeHeader(new UDADeviceType("MediaServer")));
        upnpService.getControlPoint().search(new UDADeviceTypeHeader(new UDADeviceType("MediaRenderer")));
    }

    public static class BrowseRegistryListener extends DefaultRegistryListener {

        /* Discovery performance optimization for very slow Android devices! */
        /*
         * @Override public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) {
         * deviceAdded(device); }
         * @Override public void remoteDeviceDiscoveryFailed(Registry registry, final RemoteDevice device, final
         * Exception ex) { Application.activity.showToast("Discovery failed of '" + device.getDisplayString() + "': " +
         * (ex != null ? ex.toString() : "Couldn't retrieve device/service descriptors"), true); deviceRemoved(device);
         * }
         */
        /* End of optimization, you can remove the whole block if your Android handset is fast (>= 600 Mhz) */

        @Override
        public void remoteDeviceAdded(Registry registry, RemoteDevice device) {

            deviceAdded(device);
        }
@Override
public void remoteDeviceUpdated(Registry registry, RemoteDevice device) {
    deviceAdded(device);
}
        @Override
        public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
            deviceRemoved(device);
        }

        @Override
        public void localDeviceAdded(Registry registry, LocalDevice device) {
            deviceAdded(device);
        }

        @Override
        public void localDeviceRemoved(Registry registry, LocalDevice device) {
            deviceRemoved(device);
        }

        public void deviceAdded(final Device device) {
            if (MEDIASERVER_TYPE.equals(device.getType().toString())) {
                UpnpServerDevice d = new UpnpServerDevice(device);
                if (d.equals(UpnpDeviceManager.getInstance().libraryDevice)) {
                    UpnpDeviceManager.getInstance().libraryDevice.setDevice(device);
                    d = UpnpDeviceManager.getInstance().libraryDevice;
                } else {
                    int position = UpnpDeviceManager.getInstance().lstServer.indexOf(d);
                    if (position >= 0) {
                        UpnpDeviceManager.getInstance().lstServer.get(position).setDevice(device);
                    } else {

                        UpnpDeviceManager.getInstance().lstServer.add(d);

                    }
                }

                BusManager.getInstance().post(new UpnpServerAddEvent(d));

            } else if (MEDIARENDERER_TYPE.equals(device.getType().toString())) {
                UpnpRendererDevice d = new UpnpRendererDevice(device);
                int pos = UpnpDeviceManager.getInstance().rendererDevice.indexOf(d);
                if (pos != -1) {
                    UpnpRendererDevice dd = UpnpDeviceManager.getInstance().rendererDevice.get(pos);
                    dd.setDevice(device);
                    d = dd;
                } else {
                    int position = UpnpDeviceManager.getInstance().lstRenderer.indexOf(d);
                    if (position >= 0) {
                        UpnpDeviceManager.getInstance().lstRenderer.get(position).setDevice(device);
                    } else {
                        UpnpDeviceManager.getInstance().lstRenderer.add(d);

                    }
                }

                BusManager.getInstance().post(new UpnpRendererAddEvent(d));

            }

        }

        public void deviceRemoved(final Device device) {

            if (MEDIASERVER_TYPE.equals(device.getType().toString())) {
                UpnpServerDevice serveur = new UpnpServerDevice(device);
                UpnpDeviceManager.getInstance().lstServer.remove(serveur);

                BusManager.getInstance().post(new UpnpServerRemoveEvent(serveur));

            }

            if (MEDIARENDERER_TYPE.equals(device.getType().toString())) {
                UpnpRendererDevice renderer = new UpnpRendererDevice(device);
                UpnpDeviceManager.getInstance().lstRenderer.remove(renderer);
                BusManager.getInstance().post(new UpnpRendererRemoveEvent(renderer));

            }

        }
    };

}
