package org.dyndns.fzoli.rccar.controller.socket;

import org.dyndns.fzoli.rccar.controller.ConnectionProgressFrame.Status;
import static org.dyndns.fzoli.rccar.controller.Main.showConnectionStatus;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.impl.ClientDisconnectProcess;

/**
 * A vezérlő oldalán vizsgálja, hogy él-e még a kapcsolat a klienssel.
 * @author zoli
 */
public class ControllerDisconnectProcess extends ClientDisconnectProcess {

    public ControllerDisconnectProcess(SecureHandler handler) {
        super(handler);
    }

    /**
     * Ha a kapcsolt megszakadt.
     * Az összes aktív kapcsolatfeldolgozót leállítja, mely ugyan ahhoz az eszközhöz tartozik
     * és megjeleníti a kapcsolódás hiba ablakot.
     */
    @Override
    protected void onDisconnect() {
        super.onDisconnect();
        showConnectionStatus(Status.DISCONNECTED);
    }
    
}
