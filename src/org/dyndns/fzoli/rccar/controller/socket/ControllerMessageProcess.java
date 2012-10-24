package org.dyndns.fzoli.rccar.controller.socket;

import static org.dyndns.fzoli.rccar.controller.Main.showHostSelectionFrame;
import static org.dyndns.fzoli.rccar.controller.Main.updateHostSelectionFrame;
import org.dyndns.fzoli.rccar.model.controller.HostList;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.impl.MessageProcess;

/**
 *
 * @author zoli
 */
public class ControllerMessageProcess extends MessageProcess {

    public ControllerMessageProcess(SecureHandler handler) {
        super(handler);
    }

    @Override
    protected void onMessage(Object o) {
        if (o instanceof HostList) {
            showHostSelectionFrame((HostList) o);
        }
        if (o instanceof HostList.PartialHostList) {
            updateHostSelectionFrame((HostList.PartialHostList) o);
        }
    }
    
}
