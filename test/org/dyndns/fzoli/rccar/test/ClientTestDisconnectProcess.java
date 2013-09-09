package org.dyndns.fzoli.rccar.test;

import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.impl.ClientDisconnectProcess;

/**
 * Ping szerű teszt.
 * @author zoli
 */
public class ClientTestDisconnectProcess extends ClientDisconnectProcess {

    private final DisconnectProcessTester t = new DisconnectProcessTester();
    
    public ClientTestDisconnectProcess(SecureHandler handler, int timeout1, int timeout2, int waiting) {
        super(handler, timeout1, timeout2, waiting);
    }

    @Override
    protected void onConnect() {
        System.out.println("Connected");
        super.onConnect();
    }

    @Override
    protected void onDisconnect(Exception ex) {
        t.onDisconnect();
        super.onDisconnect(ex);
    }

    @Override
    protected void onTimeout(Exception ex) throws Exception {
        t.onTimeout();
        super.onTimeout(ex);
    }

    @Override
    protected void beforeAnswer() throws Exception {
        t.beforeAnswer();
        super.beforeAnswer();
    }

    @Override
    protected void afterAnswer() throws Exception {
        t.afterAnswer();
        super.afterAnswer();
    }

    @Override
    protected void afterTimeout() throws Exception {
        System.out.println("after timeout");
        super.afterTimeout();
    }
    
}
