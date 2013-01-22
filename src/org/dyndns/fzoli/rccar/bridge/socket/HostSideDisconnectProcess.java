package org.dyndns.fzoli.rccar.bridge.socket;

import org.dyndns.fzoli.rccar.model.bridge.HostStorage;
import org.dyndns.fzoli.rccar.model.bridge.StorageList;
import org.dyndns.fzoli.socket.handler.SecureHandler;

/**
 *
 * @author zoli
 */
public class HostSideDisconnectProcess extends BridgeDisconnectProcess {

    public HostSideDisconnectProcess(SecureHandler handler) {
        super(handler);
    }

    private void setTimeout(boolean b) {
        HostStorage st = StorageList.findHostStorageByName(getRemoteCommonName());
        if (st != null) st.setUnderTimeout(b);
    }
    
    @Override
    protected void onConnect() {
        setTimeout(false);
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
    
}
