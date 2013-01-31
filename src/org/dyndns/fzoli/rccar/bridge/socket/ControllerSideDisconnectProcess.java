package org.dyndns.fzoli.rccar.bridge.socket;

import org.dyndns.fzoli.rccar.model.Control;
import org.dyndns.fzoli.rccar.model.bridge.ControllerStorage;
import org.dyndns.fzoli.rccar.model.bridge.StorageList;
import org.dyndns.fzoli.socket.handler.SecureHandler;

/**
 *
 * @author zoli
 */
public class ControllerSideDisconnectProcess extends BridgeDisconnectProcess {

    private ControllerStorage storage;
    
    public ControllerSideDisconnectProcess(SecureHandler handler) {
        super(handler);
    }

    private ControllerStorage getControllerStorage() {
        if (storage != null) return storage;
        return storage = StorageList.findControllerStorageByName(getRemoteCommonName());
    }
    
    @Override
    protected void onTimeout(Exception ex) throws Exception {
        ControllerStorage cs = getControllerStorage();
        if (cs != null) cs.getReceiver().setControl(new Control(0, 0));
        super.onTimeout(ex);
    }

    @Override
    protected void afterTimeout() throws Exception {
        // TODO: jármű vezérlés visszaállítása, ha azóta nem változott
        super.afterTimeout();
    }

    @Override
    protected void onDisconnect(Exception ex) {
        ControllerStorage cs = getControllerStorage();
        if (cs != null && cs.getHostStorage() != null) cs.setHostStorage(null);
        super.onDisconnect(ex);
    }
    
}
