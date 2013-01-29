package org.dyndns.fzoli.rccar.bridge.socket;

import java.io.Serializable;
import org.dyndns.fzoli.rccar.ConnectionKeys;
import org.dyndns.fzoli.rccar.model.PartialBaseData;
import org.dyndns.fzoli.rccar.model.bridge.ControllerStorage;
import org.dyndns.fzoli.rccar.model.bridge.StorageList;
import org.dyndns.fzoli.rccar.model.controller.ControllerData;
import org.dyndns.fzoli.socket.handler.SecureHandler;

/**
 *
 * @author zoli
 */
public class ControllerSideMessageProcess extends BridgeMessageProcess implements ConnectionKeys {

    private ControllerStorage storage;

    public ControllerSideMessageProcess(SecureHandler handler) {
        super(handler);
    }

    @Override
    protected void onStart() {
        storage = StorageList.createControllerStorage(this);
        sendMessage(StorageList.createHostList(getRemoteCommonName()));
    }

    @Override
    protected void onMessage(Serializable o) {
        if (o instanceof PartialBaseData) {
            PartialBaseData<ControllerData, ?> pd = (PartialBaseData) o;
            if (storage != null) storage.getReceiver().update(pd);
        }
    }

}
