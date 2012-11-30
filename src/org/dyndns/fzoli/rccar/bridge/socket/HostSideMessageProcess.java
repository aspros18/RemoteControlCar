package org.dyndns.fzoli.rccar.bridge.socket;

import java.util.Date;
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
    }

    @Override
    protected void onMessage(Object o) {
        System.out.println(o);
    }
    
}
