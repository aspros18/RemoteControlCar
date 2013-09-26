package org.dyndns.fzoli.rccar.bridge.socket;

import org.dyndns.fzoli.rccar.model.Control;
import org.dyndns.fzoli.rccar.model.bridge.ControllerStorage;
import org.dyndns.fzoli.rccar.model.bridge.StorageList;
import org.dyndns.fzoli.socket.handler.SecureHandler;

/**
 * A vezérlő-kliens szerver oldali kapcsolatfigyelő.
 * @author zoli
 */
public class ControllerSideDisconnectProcess extends BridgeDisconnectProcess {

    /**
     * A vezérlő munkamenete.
     */
    private ControllerStorage storage;
    
    /**
     * Az időtúllépéskori vezérlőjel.
     */
    private Control prevControl;
    
    /**
     * Az időtúllépéskori vezérlőjel-számláló.
     */
    private Integer prevCount;
    
    public ControllerSideDisconnectProcess(SecureHandler handler) {
        super(handler);
    }

    /**
     * A vezérlő munkamenetét adja vissza.
     * Ha még nincs meg a referencia, megkeresi azt.
     */
    private ControllerStorage getControllerStorage() {
        if (storage != null) return storage;
        return storage = StorageList.findControllerStorageByName(getRemoteCommonName());
    }
    
    /**
     * Ha a vezérlő kapcsolatában időtúllépés van, eltárolja az aktuális vezérlőjelet és megállítja a járművet.
     */
    @Override
    protected void onTimeout(Exception ex) throws Exception {
        ControllerStorage cs = getControllerStorage();
        if (cs != null) {
            if (cs.getHostStorage() != null) {
                prevControl = cs.getHostStorage().getHostData().getControl();
                prevCount = cs.getHostStorage().getControlCount();
            }
            cs.getReceiver().setControl(new Control(0, 0));
        }
        super.onTimeout(ex);
    }

    /**
     * Ha a vezérlő kapcsolata helyreállt, akkor az eltárolt vezérlőjelet visszaállítja, ha azóta nem változott meg.
     */
    @Override
    protected void afterTimeout() throws Exception {
        ControllerStorage cs = getControllerStorage();
        if (cs != null && cs.getHostStorage() != null && prevControl != null && prevCount != null && prevCount.equals(cs.getHostStorage().getControlCount())) {
            cs.getReceiver().setControl(prevControl);
        }
        super.afterTimeout();
    }

    /**
     * Ha a vezérlő lekapcsolódott a szerverről, és járműhöz tartozik, leválasztódik róla
     * és eltárolódik, hogy a legközelebbi kapcsolódáskor ne kelljen járművet választani újra.
     */
    @Override
    protected void onDisconnect(Exception ex) {
        ControllerStorage cs = getControllerStorage();
        if (cs != null && cs.getHostStorage() != null) {
            cs.storeDisconnectedHost();
            cs.setHostStorage(null);
        }
        super.onDisconnect(ex);
    }
    
}
