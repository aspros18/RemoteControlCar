package org.dyndns.fzoli.rccar.bridge.socket;

import java.io.Serializable;
import java.util.List;
import org.dyndns.fzoli.rccar.model.PartialBaseData;
import org.dyndns.fzoli.rccar.model.bridge.ControllerStorage;
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
    protected void onMessage(Serializable o) { // TODO: Data szintre kéne helyezni egy boolean paramétert, ami eldönti, hogy legyen-e üzenetküldés a setter(ek)ben. Az elv, hogy a Data setterei küldenek üzenetet, de ez alól a küldő kivétel. (És van olyan setter, ami küld Host oldalra és Controller oldalra is, de olyan is ami csak az egyik oldalra.)
        if (o instanceof HostData) {
            storage = StorageList.createHostStorage(this, (HostData) o);
        }
        else if (o instanceof PartialBaseData) {
            PartialBaseData pd = (PartialBaseData) o;
            if (storage != null) {
                // TODO: pd alapján üzenet generálás a controllerek számára
                storage.getHostData().update(pd);
                List<ControllerStorage> ls = StorageList.getControllerStorageList();
                for (ControllerStorage s : ls) {
                    HostStorage hs = s.getHostStorage();
                    if (hs == null) continue;
                    if (hs == storage) {
                        // TODO: generált üzenet küldése
                    }
                }
            }
        }
    }
    
}
