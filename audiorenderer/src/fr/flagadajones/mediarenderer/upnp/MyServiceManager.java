package fr.flagadajones.mediarenderer.upnp;

import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.support.lastchange.LastChangeAwareServiceManager;
import org.fourthline.cling.support.lastchange.LastChangeDelegator;
import org.fourthline.cling.support.lastchange.LastChangeParser;

public class MyServiceManager<T extends LastChangeDelegator> extends LastChangeAwareServiceManager<T> {

    public MyServiceManager(LocalService<T> localService, LastChangeParser lastChangeParser) {
        this(localService, null, lastChangeParser);
    }

    public MyServiceManager(LocalService<T> localService, Class<T> serviceClass, LastChangeParser lastChangeParser) {
        super(localService, serviceClass, lastChangeParser);

    }

    @Override
    protected int getLockTimeoutMillis() {
        return 2000;
    }
}
