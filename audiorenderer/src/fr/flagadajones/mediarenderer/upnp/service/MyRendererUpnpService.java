package fr.flagadajones.mediarenderer.upnp.service;

import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.android.AndroidUpnpServiceConfiguration;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.binding.xml.DeviceDescriptorBinder;
import org.fourthline.cling.binding.xml.RecoveringUDA10DeviceDescriptorBinderImpl;
import org.fourthline.cling.binding.xml.ServiceDescriptorBinder;
import org.fourthline.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl;

/**
 * @author Christian Bauer
 */
public class MyRendererUpnpService extends AndroidUpnpServiceImpl {

    @Override
    protected UpnpServiceConfiguration createConfiguration() {
//        // TODO Auto-generated method stub
        return new AndroidUpnpServiceConfiguration() {
//@Override
//public DeviceDescriptorBinder getDeviceDescriptorBinderUDA10() {
//	// TODO Auto-generated method stub
//	return new RecoveringUDA10DeviceDescriptorBinderImpl();
//}
//
//@Override
//			public ServiceDescriptorBinder getServiceDescriptorBinderUDA10() {
//				// TODO Auto-generated method stub
//				return new UDA10ServiceDescriptorBinderSAXImpl();
//			}
            /*
             * The only purpose of this class is to show you how you'd configure the AndroidUpnpServiceImpl in your
             * application:
             * @Override public int getRegistryMaintenanceIntervalMillis() { return 7000; }
             * @Override public ServiceType[] getExclusiveServiceTypes() { return new ServiceType[] { new
             * UDAServiceType("SwitchPower") }; }
             */

        };
    }
    

}
