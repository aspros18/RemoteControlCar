package org.dyndns.fzoli.rccar.test;

import org.dyndns.fzoli.socket.handler.AbstractSecureServerHandler;
import org.dyndns.fzoli.socket.process.impl.ServerMessageProcess;

/**
 * Üzenetküldő tesztelése szerver oldalon, két szálon.
 * @author zoli
 */
public class ServerMessageTestProcess extends ServerMessageProcess {

    public ServerMessageTestProcess(AbstractSecureServerHandler handler) {
        super(handler);
    }
    
    @Override
    public void onStart() {
        ClientMessageTestProcess.onStart(this);
    }

    @Override
    public void onMessage(Object o) {
        ClientMessageTestProcess.onMessage(this, o);
    }

    @Override
    public void onException(Exception ex) {
        ClientMessageTestProcess.onException(this, ex);
        super.onException(ex);
    }
    
}
