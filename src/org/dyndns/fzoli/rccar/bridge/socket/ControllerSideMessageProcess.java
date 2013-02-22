package org.dyndns.fzoli.rccar.bridge.socket;

import java.io.Serializable;
import org.dyndns.fzoli.rccar.ConnectionKeys;
import org.dyndns.fzoli.rccar.model.PartialBaseData;
import org.dyndns.fzoli.rccar.model.bridge.ControllerStorage;
import org.dyndns.fzoli.rccar.model.bridge.HostStorage;
import org.dyndns.fzoli.rccar.model.bridge.StorageList;
import org.dyndns.fzoli.rccar.model.controller.ControllerData;
import org.dyndns.fzoli.socket.handler.SecureHandler;

/**
 * A vezérlő-kliens szerver oldali üzenetfeldolgozója.
 * @author zoli
 */
public class ControllerSideMessageProcess extends BridgeMessageProcess implements ConnectionKeys {

    /**
     * A vezérlő kliens munkamenete a szerveren.
     */
    private ControllerStorage storage;

    public ControllerSideMessageProcess(SecureHandler handler) {
        super(handler);
    }

    /**
     * Kapcsolódáskor létrehozza/frissíti a vezérlő munkamenetét és ha nincs leválasztott járműje, elküldi a járműlistát a vezérlőnek,
     * egyébként visszaállítja a járművet és elküldi a járműadatokat.
     * A vezérlő oldalon ezáltal megjelenik a járműválasztó ablak vagy a régebben kiválasztott járműhöz tartozó felület.
     */
    @Override
    protected void onStart() {
        storage = StorageList.createControllerStorage(this);
        if (!storage.hasDisconnectedHost()) {
            sendMessage(StorageList.createHostList(getRemoteCommonName()));
        }
        else {
            storage.restoreDisconnectedHost();
            sendMessage(storage.createControllerData());
        }
    }

    /**
     * Ha részadat érkezik a klienstől, átadja a munkamenet üzenetfogadójának.
     */
    @Override
    protected void onMessage(Serializable o) {
        if (o instanceof PartialBaseData) {
            PartialBaseData<ControllerData, ?> pd = (PartialBaseData) o;
            if (storage != null) storage.getReceiver().update(pd);
        }
    }

}
