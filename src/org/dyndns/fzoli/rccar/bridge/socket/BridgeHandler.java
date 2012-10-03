package org.dyndns.fzoli.rccar.bridge.socket;

import javax.net.ssl.SSLSocket;
import static org.dyndns.fzoli.rccar.bridge.Main.showWarning;
import org.dyndns.fzoli.rccar.test.DummyProcess;
import org.dyndns.fzoli.socket.handler.AbstractSecureServerHandler;
import org.dyndns.fzoli.socket.handler.MultipleCertificateException;
import org.dyndns.fzoli.socket.process.AbstractSecureProcess;

/**
 * A híd kapcsolatkezelője.
 * TODO: egyelőre teszt
 * @author zoli
 */
public class BridgeHandler extends AbstractSecureServerHandler {

    public BridgeHandler(SSLSocket socket) {
        super(socket);
    }

    @Override
    protected void onException(Exception ex) {
        try {
            throw ex;
        }
        catch (MultipleCertificateException e) {
            showWarning(getSocket(), "Duplázott tanúsítvány");
        }
        catch (Exception e) {
            super.onException(e);
        }
    }

    @Override
    protected AbstractSecureProcess selectProcess() {
        switch (getConnectionId()) {
            case 1:
                return new DummyProcess(this);
            default:
                return new BridgeDisconnectProcess(this);
        }
    }
    
}
