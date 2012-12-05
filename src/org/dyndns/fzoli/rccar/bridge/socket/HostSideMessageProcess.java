package org.dyndns.fzoli.rccar.bridge.socket;

import java.util.Date;
import org.dyndns.fzoli.rccar.model.Control;
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
        System.out.println(new Date() + " host connected");
        // MJPEG streamelés aktiválása teszt céljából:
        sendMessage(new HostData.BooleanPartialHostData(Boolean.TRUE, HostData.BooleanPartialHostData.BooleanType.STREAMING));
        // jármű vezérlőjel küldése teszt céljából:
        sendMessage(new HostData.ControlPartialHostData(new Control(50, 50)));
    }

    @Override
    protected void onMessage(Object o) {
        System.out.println(o);
    }
    
}
