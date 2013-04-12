package fr.fladajonesjones.MediaControler.upnp;


import org.fourthline.cling.android.AndroidUpnpServiceConfiguration;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;

/**
 * @author Christian Bauer
 */
public class UpnpService extends AndroidUpnpServiceImpl {

	
    @Override
    protected AndroidUpnpServiceConfiguration createConfiguration() {
        return new AndroidUpnpServiceConfiguration() {
            

            /* The only purpose of this class is to show you how you'd
              configure the AndroidUpnpServiceImpl in your application:

           @Override
           public int getRegistryMaintenanceIntervalMillis() {
               return 7000;
           }

           @Override
           public ServiceType[] getExclusiveServiceTypes() {
               return new ServiceType[] {
                       new UDAServiceType("SwitchPower")
               };
           }

            */

        };
    }
    
   

}
