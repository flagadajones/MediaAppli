package fr.flagadajones.mediarenderer.upnp.service;

import org.fourthline.cling.support.connectionmanager.ConnectionManagerService;
import org.fourthline.cling.support.model.ProtocolInfo;


public class MyConnectionManagerService extends ConnectionManagerService {

    public boolean isConnected(){
        return !activeConnections.isEmpty();
    }
	public MyConnectionManagerService() {
//		MimeType mt = MimeType.valueOf("http-get:*:audio/mpegurl:*");
//		sinkProtocolInfo.add(new ProtocolInfo(mt));
//		mt = MimeType.valueOf("http-get:*:audio/mp3:*");
//		sinkProtocolInfo.add(new ProtocolInfo(mt));
//		mt = MimeType.valueOf("http-get:*:audio/mpeg:*");
//		sinkProtocolInfo.add(new ProtocolInfo(mt));
//		mt = MimeType.valueOf("http-get:*:audio/x-ms-wma:*");
//		sinkProtocolInfo.add(new ProtocolInfo(mt));
//		mt = MimeType.valueOf("http-get:*:audio/wma:*");
//		sinkProtocolInfo.add(new ProtocolInfo(mt));
//		mt = MimeType.valueOf("http-get:*:audio/mpeg3:*");
//		sinkProtocolInfo.add(new ProtocolInfo(mt));
//		mt = MimeType.valueOf("http-get:*:video/x-ms-wmv:*");
//		sinkProtocolInfo.add(new ProtocolInfo(mt));
//		mt = MimeType.valueOf("http-get:*:video/x-ms-asf:*");
//		sinkProtocolInfo.add(new ProtocolInfo(mt));
//		mt = MimeType.valueOf("http-get:*:video/x-ms-avi:*");
//		sinkProtocolInfo.add(new ProtocolInfo(mt));
//		mt = MimeType.valueOf("http-get:*:video/mpeg:*");
//		sinkProtocolInfo.add(new ProtocolInfo(mt));
//		
//		mt = MimeType.valueOf("http-get:*:audio/m3u:*");
//        sinkProtocolInfo.add(new ProtocolInfo(mt));
//		
	    
        sinkProtocolInfo.add(new ProtocolInfo("http-get:*:audio/mpegurl:*"));
        sinkProtocolInfo.add(new ProtocolInfo("http-get:*:audio/mp3:*"));
        sinkProtocolInfo.add(new ProtocolInfo("http-get:*:audio/mpeg:*"));
        sinkProtocolInfo.add(new ProtocolInfo("http-get:*:audio/x-ms-wma:*"));
        sinkProtocolInfo.add(new ProtocolInfo("http-get:*:audio/wma:*"));
        sinkProtocolInfo.add(new ProtocolInfo("http-get:*:audio/wmp12:*"));
        sinkProtocolInfo.add(new ProtocolInfo("http-get:*:audio/mpeg3:*"));
        sinkProtocolInfo.add(new ProtocolInfo("http-get:*:video/x-ms-wmv:*"));
        sinkProtocolInfo.add(new ProtocolInfo("http-get:*:video/x-ms-asf:*"));
        sinkProtocolInfo.add(new ProtocolInfo("http-get:*:video/x-ms-avi:*"));
        sinkProtocolInfo.add(new ProtocolInfo("http-get:*:video/mpeg:*"));
        
        sinkProtocolInfo.add(new ProtocolInfo("http-get:*:audio/m3u:*"));
        

        
        
	}
}
