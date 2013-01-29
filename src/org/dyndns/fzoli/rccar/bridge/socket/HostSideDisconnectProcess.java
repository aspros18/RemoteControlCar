package org.dyndns.fzoli.rccar.bridge.socket;

import java.util.List;
import org.dyndns.fzoli.rccar.model.bridge.ControllerStorage;
import org.dyndns.fzoli.rccar.model.bridge.HostStorage;
import org.dyndns.fzoli.rccar.model.bridge.StorageList;
import org.dyndns.fzoli.rccar.model.controller.HostList;
import org.dyndns.fzoli.rccar.model.controller.HostList.PartialHostList;
import org.dyndns.fzoli.socket.handler.SecureHandler;

/**
 *
 * @author zoli
 */
public class HostSideDisconnectProcess extends BridgeDisconnectProcess {
    
    public HostSideDisconnectProcess(SecureHandler handler) {
        super(handler);
    }

    private void sendConnectionMessage(boolean connected) {
        PartialHostList msg = new HostList.PartialHostList(getRemoteCommonName(), connected ? HostList.PartialHostList.ChangeType.ADD : HostList.PartialHostList.ChangeType.REMOVE);
        List<ControllerStorage> controllers = StorageList.getControllerStorageList();
        for (ControllerStorage cs : controllers) {
            if (cs.getHostStorage() == null) cs.getMessageProcess().sendMessage(msg);
        }
    }
    
    private void setTimeout(boolean b) {
        HostStorage st = StorageList.findHostStorageByName(getRemoteCommonName());
        if (st != null) st.setUnderTimeout(b);
    }
    
    @Override
    protected void onConnect() {
        sendConnectionMessage(true);
        super.onConnect();
    }

    @Override
    protected void onTimeout(Exception ex) throws Exception {
        setTimeout(true);
        super.onTimeout(ex);
    }

    @Override
    protected void afterTimeout() throws Exception {
        setTimeout(false);
        super.afterTimeout();
    }

    @Override
    protected void onDisconnect(Exception ex) {
        sendConnectionMessage(false);
        setTimeout(false);
        super.onDisconnect(ex);
    }
    
}
