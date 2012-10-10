package org.dyndns.fzoli.rccar.controller.socket;

import javax.net.ssl.SSLSocket;
import static org.dyndns.fzoli.rccar.controller.Main.PROGRESS_FRAME;
import org.dyndns.fzoli.rccar.test.DummyProcess;
import org.dyndns.fzoli.socket.handler.AbstractSecureClientHandler;
import org.dyndns.fzoli.socket.handler.RemoteHandlerException;
import org.dyndns.fzoli.socket.process.SecureProcess;

/**
 * A vezérlő kapcsolatkezelője.
 * @author zoli
 */
public class ControllerHandler extends AbstractSecureClientHandler {

    /**
     * Szép konstruktor.
     */
    public ControllerHandler(SSLSocket socket, int deviceId, int connectionId) {
        super(socket, deviceId, connectionId);
    }

    @Override
    protected void onException(Exception ex) {
        try {
            throw ex;
        }
        catch (RemoteHandlerException e) {
            PROGRESS_FRAME.setRefused(true);
            PROGRESS_FRAME.setVisible(true);
        }
        catch (Exception e) {
            super.onException(e);
        }
    }
    
    /**
     * A kapcsolatazonosító alapján eldől, melyik feldolgozót kell indítani.
     * Egyelőre csak teszt.
     */
    @Override
    protected SecureProcess selectProcess() {
        switch (getConnectionId()) {
            case 0:
                return new ControllerDisconnectProcess(this);
            default:
                return new DummyProcess(this);
        }
    }
    
}
