package org.dyndns.fzoli.rccar.bridge.socket;

import java.io.Serializable;
import org.dyndns.fzoli.rccar.model.PartialBaseData;
import org.dyndns.fzoli.rccar.model.bridge.HostStorage;
import org.dyndns.fzoli.rccar.model.bridge.StorageList;
import org.dyndns.fzoli.rccar.model.host.HostData;
import org.dyndns.fzoli.socket.handler.SecureHandler;

/**
 *
 * @author zoli
 */
public class HostSideMessageProcess extends BridgeMessageProcess {

    private HostStorage storage;
    
    public HostSideMessageProcess(SecureHandler handler) {
        super(handler);
    }

    @Override
    protected void onStart() {
        // MJPEG streamelés aktiválása teszt céljából:
        sendMessage(new HostData.BooleanPartialHostData(Boolean.TRUE, HostData.BooleanPartialHostData.BooleanType.STREAMING));
    }

    @Override
    protected void onMessage(Serializable o) {
        if (o instanceof HostData) {
            storage = StorageList.createHostStorage(this, (HostData) o);
        }
        else if (o instanceof PartialBaseData) {
            PartialBaseData<HostData, ?> pd = (PartialBaseData) o;
            if (storage != null) storage.getReceiver().update(pd);
        }
    }
    
}
