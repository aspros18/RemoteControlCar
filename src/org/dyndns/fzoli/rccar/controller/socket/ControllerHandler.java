package org.dyndns.fzoli.rccar.controller.socket;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSocket;
import org.dyndns.fzoli.rccar.controller.ConnectionProgressFrame.Status;
import static org.dyndns.fzoli.rccar.controller.Main.showConnectionStatus;
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
            showConnectionStatus(Status.REFUSED);
        }
        catch (SocketTimeoutException e) {
            showConnectionStatus(Status.CONNECTION_TIMEOUT);
        }
        catch (SSLHandshakeException e) {
            showConnectionStatus(Status.HANDSHAKE_ERROR);
        }
        catch (SocketException e) {
            showConnectionStatus(Status.CONNECTION_ERROR);
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
                return new ControllerMessageProcess(this);
        }
    }
    
}
