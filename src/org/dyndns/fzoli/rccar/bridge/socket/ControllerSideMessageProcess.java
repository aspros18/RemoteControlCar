package org.dyndns.fzoli.rccar.bridge.socket;

import org.dyndns.fzoli.rccar.model.controller.HostList;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.impl.MessageProcess;

/**
 *
 * @author zoli
 */
public class ControllerSideMessageProcess extends MessageProcess {

    public ControllerSideMessageProcess(SecureHandler handler) {
        super(handler);
    }

    @Override
    protected void onStart() {
        HostList l = new HostList();
        l.addHost("teszt");
        sendMessage(l);
    }

    @Override
    protected void onMessage(Object o) {
        ;
    }
    
}
