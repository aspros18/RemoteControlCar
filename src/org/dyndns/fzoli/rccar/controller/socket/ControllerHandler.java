package org.dyndns.fzoli.rccar.controller.socket;

import java.io.EOFException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSocket;
import org.dyndns.fzoli.rccar.ConnectionKeys;
import static org.dyndns.fzoli.rccar.controller.Main.showConnectionStatus;
import org.dyndns.fzoli.rccar.controller.view.ConnectionProgressFrame.Status;
import org.dyndns.fzoli.socket.handler.AbstractSecureClientHandler;
import org.dyndns.fzoli.socket.handler.exception.RemoteHandlerException;
import org.dyndns.fzoli.socket.process.SecureProcess;

/**
 * A vezérlő kapcsolatkezelője.
 * @author zoli
 */
public class ControllerHandler extends AbstractSecureClientHandler implements ConnectionKeys {

    /**
     * Szép konstruktor.
     */
    public ControllerHandler(SSLSocket socket, int deviceId, int connectionId) {
        super(socket, deviceId, connectionId);
    }

    /**
     * A kapcsolatkezelő ablak figyelmezteti a felhasználót, hogy kivétel keletkezett.
     * A keletkezett kivételnek megfelelően változik a kapcsolatkezelő ablak üzenete.
     */
    @Override
    protected void onException(Exception ex) {
        try {
            throw ex;
        }
        catch (RemoteHandlerException e) {
            showConnectionStatus(Status.CONNECTION_REFUSED);
        }
        catch (SocketTimeoutException e) {
            showConnectionStatus(Status.CONNECTION_TIMEOUT);
        }
        catch (SSLHandshakeException e) {
            showConnectionStatus(e.getMessage().contains("Extended key usage") ? Status.SERVER_IS_NOT_CLIENT : Status.HANDSHAKE_ERROR);
        }
        catch (SocketException e) {
            showConnectionStatus(Status.CONNECTION_ERROR);
        }
        catch (EOFException e) {
            showConnectionStatus(Status.DISCONNECTED);
        }
        catch (Exception e) {
            super.onException(e);
        }
    }
    
    /**
     * A kapcsolatazonosító alapján eldől, melyik feldolgozót kell indítani.
     */
    @Override
    protected SecureProcess selectProcess() {
        switch (getConnectionId()) {
            case KEY_CONN_DISCONNECT:
                return new ControllerDisconnectProcess(this);
            case KEY_CONN_MESSAGE:
                return new ControllerMessageProcess(this);
            case KEY_CONN_VIDEO_STREAM:
                return new ControllerVideoProcess(this);
        }
        return null;
    }
    
}
