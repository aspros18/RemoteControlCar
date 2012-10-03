package org.dyndns.fzoli.rccar.bridge.socket;

import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.impl.ServerDisconnectProcess;

/**
 * A híd oldalán vizsgálja, hogy él-e még a kapcsolat a klienssel.
 * TODO: egyelőre teszt
 * @author zoli
 */
public class BridgeDisconnectProcess extends ServerDisconnectProcess {

    public BridgeDisconnectProcess(SecureHandler handler) {
        super(handler);
    }

    @Override
    protected void onDisconnect() {
        System.out.println("MEGSZAKADT A KAPCSOLAT A KLIENSSEL");
    }
    
}
