package org.dyndns.fzoli.rccar.bridge.socket;

import org.dyndns.fzoli.rccar.model.Control;
import org.dyndns.fzoli.rccar.model.bridge.StorageList;
import org.dyndns.fzoli.rccar.model.host.HostData;
import org.dyndns.fzoli.socket.handler.SecureHandler;

/**
 *
 * @author zoli
 */
public class HostSideMessageProcess extends BridgeMessageProcess {

    public HostSideMessageProcess(SecureHandler handler) {
        super(handler);
    }

    @Override
    protected void onStart() {
        // MJPEG streamelés aktiválása teszt céljából:
        sendMessage(new HostData.BooleanPartialHostData(Boolean.TRUE, HostData.BooleanPartialHostData.BooleanType.STREAMING));
        // jármű vezérlőjel küldése teszt céljából:
        sendMessage(new HostData.ControlPartialHostData(new Control(50, 50)));
    }

    @Override
    protected void onMessage(Object o) {
        if (o instanceof HostData) {
            StorageList.createHostStorage(this, (HostData) o);
        }
        else if (o instanceof HostData.PointPartialHostData) {
            HostData.PointPartialHostData pd = (HostData.PointPartialHostData) o;
            System.out.println("point data length: " + pd.data.length + "\n\n\n");
        }
        else {
            System.out.println(o);
        }
    }
    
}
