package org.dyndns.fzoli.rccar.bridge.socket;

import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.AbstractSecureProcess;

/**
 *
 * @author zoli
 */
public class ControllerSideVideoProcess extends AbstractSecureProcess {

    public ControllerSideVideoProcess(SecureHandler handler) {
        super(handler);
    }

    @Override
    public void run() {
        try {
            getSocket().getInputStream().read();
        }
        catch (Exception ex) {
        }
    }
    
}
