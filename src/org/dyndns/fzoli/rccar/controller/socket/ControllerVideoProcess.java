package org.dyndns.fzoli.rccar.controller.socket;

import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.AbstractSecureProcess;

/**
 *
 * @author zoli
 */
public class ControllerVideoProcess extends AbstractSecureProcess {

    public ControllerVideoProcess(SecureHandler handler) {
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
