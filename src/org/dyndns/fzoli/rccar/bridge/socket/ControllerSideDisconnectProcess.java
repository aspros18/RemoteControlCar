package org.dyndns.fzoli.rccar.bridge.socket;

import org.dyndns.fzoli.rccar.model.bridge.ControllerStorage;
import org.dyndns.fzoli.rccar.model.bridge.StorageList;
import org.dyndns.fzoli.socket.handler.SecureHandler;

/**
 *
 * @author zoli
 */
public class ControllerSideDisconnectProcess extends BridgeDisconnectProcess {

    public ControllerSideDisconnectProcess(SecureHandler handler) {
        super(handler);
    }

    @Override
    protected void onTimeout(Exception ex) throws Exception {
        // TODO: jármű megállítása, ha van jármű
        super.onTimeout(ex);
    }

    @Override
    protected void afterTimeout() throws Exception {
        // TODO: jármű vezérlés visszaállítása, ha azóta nem változott
        super.afterTimeout();
    }

    @Override
    protected void onDisconnect(Exception ex) {
        ControllerStorage cs = StorageList.findControllerStorageByName(getRemoteCommonName());
        if (cs != null && cs.getHostStorage() != null) cs.setHostStorage(null);
        super.onDisconnect(ex);
    }
    
}
