package org.dyndns.fzoli.rccar.bridge.socket;

import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.impl.SharedJpegProvider;
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
            SharedJpegProvider.handleConnection("test", getSocket().getOutputStream());
        }
        catch (Exception ex) {
            getHandler().closeProcesses();
        }
    }
    
}
