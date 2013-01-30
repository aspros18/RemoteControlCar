package org.dyndns.fzoli.rccar.bridge.socket;

import java.util.List;
import org.dyndns.fzoli.rccar.model.bridge.ControllerStorage;
import org.dyndns.fzoli.rccar.model.bridge.HostStorage;
import org.dyndns.fzoli.rccar.model.bridge.StorageList;
import org.dyndns.fzoli.rccar.model.controller.HostList;
import org.dyndns.fzoli.socket.handler.SecureHandler;

/**
 *
 * @author zoli
 */
public class HostSideDisconnectProcess extends BridgeDisconnectProcess {
    
    private HostStorage storage;
    
    private List<ControllerStorage> controllers = StorageList.getControllerStorageList();
    
    public HostSideDisconnectProcess(SecureHandler handler) {
        super(handler);
    }

    private HostStorage getHostStorage() {
        if (storage != null) return storage;
        return storage = StorageList.findHostStorageByName(getRemoteCommonName());
    }
    
    private void sendConnectionMessage(boolean connected) {
        HostStorage hs = getHostStorage();
        if (hs == null) return;
        hs.setConnected(connected);
        HostList.PartialHostList msgLs = new HostList.PartialHostList(getRemoteCommonName(), connected ? HostList.PartialHostList.ChangeType.ADD : HostList.PartialHostList.ChangeType.REMOVE);
        for (ControllerStorage cs : controllers) {
            if (cs.getHostStorage() == null || !hs.isConnected()) cs.getMessageProcess().sendMessage(msgLs);
            if (cs.getHostStorage() == hs) cs.getMessageProcess().sendMessage(connected ? cs.createControllerData() : StorageList.createHostList(cs.getName()));
        }
    }
    
    private void setTimeout(boolean b) {
        HostStorage hs = getHostStorage();
        if (hs != null) hs.setUnderTimeout(b);
    }
    
    @Override
    protected void onConnect() {
        super.onConnect();
        sendConnectionMessage(true);
    }

    @Override
    protected void onTimeout(Exception ex) throws Exception {
        super.onTimeout(ex);
        setTimeout(true);
    }

    @Override
    protected void afterTimeout() throws Exception {
        super.afterTimeout();
        setTimeout(false);
    }

    @Override
    protected void onDisconnect(Exception ex) {
        super.onDisconnect(ex);
        sendConnectionMessage(false);
        setTimeout(false);
    }
    
}
