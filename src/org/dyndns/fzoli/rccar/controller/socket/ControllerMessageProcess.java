package org.dyndns.fzoli.rccar.controller.socket;

import org.dyndns.fzoli.rccar.controller.ControllerModels;
import org.dyndns.fzoli.rccar.model.Data;
import org.dyndns.fzoli.rccar.model.PartialData;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.impl.MessageProcess;

/**
 * A vezérlő üzenetküldő és üzenetfogadó osztálya.
 * @author zoli
 */
public class ControllerMessageProcess extends MessageProcess {

    public ControllerMessageProcess(SecureHandler handler) {
        super(handler);
    }

    /**
     * Az üzenet feldolgozását a ControllerModels végzi.
     */
    @Override
    protected void onMessage(Object o) {
        if (o instanceof Data) {
            ControllerModels.update((Data) o);
        }
        else if (o instanceof PartialData) {
            ControllerModels.update((PartialData) o);
        }
    }
    
}
