package org.dyndns.fzoli.rccar.bridge.socket;

import java.io.Serializable;
import java.util.List;
import org.dyndns.fzoli.rccar.bridge.config.Permissions;
import org.dyndns.fzoli.rccar.model.PartialBaseData;
import org.dyndns.fzoli.rccar.model.bridge.ControllerStorage;
import org.dyndns.fzoli.rccar.model.bridge.HostStorage;
import org.dyndns.fzoli.rccar.model.bridge.StorageList;
import org.dyndns.fzoli.rccar.model.controller.ControllerData;
import org.dyndns.fzoli.rccar.model.controller.HostList;
import org.dyndns.fzoli.rccar.model.host.HostData;
import org.dyndns.fzoli.socket.handler.SecureHandler;

/**
 *
 * @author zoli
 */
public class HostSideMessageProcess extends BridgeMessageProcess {

    private HostStorage storage;
    
    private List<ControllerStorage> controllers = StorageList.getControllerStorageList();
    
    public HostSideMessageProcess(SecureHandler handler) {
        super(handler);
    }

    @Override
    protected void onStop() {
        super.onStop();
        sendConnectionMessage(false);
    }
    
    private void sendConnectionMessage(boolean connected) {
        if (storage == null) return;
        if (connected) storage.getSender().setStreaming(!storage.getControllers().isEmpty()); // a jármű lekapcsolódása után még maradhatnak bent vezérlők és így újra kapcsolódva, máris streamelni kell; másrészről egy üzenetküldéssel kiderül a kapcsolódáskor, hogy megfelelő-e a kliens verziója
        storage.setConnected(connected);
        HostList.PartialHostList msgLs = new HostList.PartialHostList(getRemoteCommonName(), connected ? HostList.PartialHostList.ChangeType.ADD : HostList.PartialHostList.ChangeType.REMOVE);
        ControllerData.BoolenPartialControllerData msgConn = new ControllerData.BoolenPartialControllerData(connected, ControllerData.BoolenPartialControllerData.BooleanType.CONNECTED);
        ControllerData.OfflineChangeablePartialControllerData msgFix = new ControllerData.OfflineChangeablePartialControllerData(new ControllerData.OfflineChangeableDatas(storage.getHostData().isFullX(), storage.getHostData().isFullY(), storage.getHostData().isVehicleConnected(), storage.getHostData().isUp2Date(), ControllerStorage.createHostState(storage)));
        for (ControllerStorage cs : controllers) {
            if (Permissions.getConfig().isEnabled(getRemoteCommonName(), cs.getName())) {
                if (cs.getHostStorage() == null) {
                    cs.getMessageProcess().sendMessage(msgLs);
                }
                if (cs.getHostStorage() == storage) {
                    cs.getMessageProcess().sendMessage(msgFix);
                    cs.getMessageProcess().sendMessage(msgConn);
                }
            }
        }
    }
    
    @Override
    protected void onMessage(Serializable o) {
        if (o instanceof HostData) {
            storage = StorageList.createHostStorage(this, (HostData) o);
            sendConnectionMessage(true);
        }
        else if (o instanceof PartialBaseData) {
            PartialBaseData<HostData, ?> pd = (PartialBaseData) o;
            if (storage != null) storage.getReceiver().update(pd);
        }
    }
    
}
