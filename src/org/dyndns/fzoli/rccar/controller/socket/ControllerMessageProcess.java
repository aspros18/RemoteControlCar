package org.dyndns.fzoli.rccar.controller.socket;

import static org.dyndns.fzoli.rccar.controller.Main.showHostSelectionFrame;
import static org.dyndns.fzoli.rccar.controller.Main.updateHostSelectionFrame;
import org.dyndns.fzoli.rccar.model.PartialBaseData;
import org.dyndns.fzoli.rccar.model.controller.ControllerData;
import org.dyndns.fzoli.rccar.model.controller.HostList;
import org.dyndns.fzoli.socket.handler.AbstractSecureClientHandler;
import org.dyndns.fzoli.socket.process.impl.ClientMessageProcess;

/**
 *
 * @author zoli
 */
public class ControllerMessageProcess extends ClientMessageProcess {

    public ControllerMessageProcess(AbstractSecureClientHandler handler) {
        super(handler);
    }

    @Override
    public void onMessage(Object o) {
        if (o instanceof HostList) {
            showHostSelectionFrame((HostList) o);
        }
        else if (o instanceof HostList.PartialHostList) {
            updateHostSelectionFrame((HostList.PartialHostList) o);
        }
        else if (o instanceof ControllerData) {
            //TODO
        }
        else if (o instanceof PartialBaseData) {
            //TODO
        }
    }
    
}
