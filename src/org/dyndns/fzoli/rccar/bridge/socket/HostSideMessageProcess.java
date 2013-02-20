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
 * A jármű-kliens szerver oldali üzenetfeldolgozója.
 * @author zoli
 */
public class HostSideMessageProcess extends BridgeMessageProcess {

    /**
     * A jármű munkamenete a szerveren.
     */
    private HostStorage storage;
    
    /**
     * A vezérlők munkameneteit tartalmazó lista.
     */
    private List<ControllerStorage> controllers = StorageList.getControllerStorageList();
    
    public HostSideMessageProcess(SecureHandler handler) {
        super(handler);
    }

    /**
     * Kapcsolódás után a szerver 1 percet ad a kliensnek, hogy legenerálja a kezdeti adatokat és átküldje azt.
     * Ha időn belül nem érkezik adat, a szerver bontja a klienssel az összes kapcsolatot.
     */
    @Override
    protected void onStart() {
        super.onStart();
        new Thread(new Runnable() {

            @Override
            public void run() {
                sleep(60000);
                if (storage == null) {
                    getHandler().closeProcesses();
                }
            }
            
        }).start();
    }

    /**
     * Ha a jármű lekapcsolódik a szerverről, jelzi azt a hozzá tartozó vezérlőknek.
     */
    @Override
    protected void onStop() {
        super.onStop();
        sendConnectionMessage(false);
    }
    
    private void sendConnectionMessage(boolean connected) {
        if (storage == null) return;
        if (connected) storage.getSender().setStreaming(!storage.getControllers().isEmpty()); // a jármű lekapcsolódása után még maradhatnak bent vezérlők és így újra kapcsolódva, máris streamelni kell; másrészről egy üzenetküldéssel kiderül a kapcsolódáskor, hogy megfelelő-e a kliens verziója
        storage.setConnected(connected); // változás mentése a munkamenetben
        HostList.PartialHostList msgLs = new HostList.PartialHostList(getRemoteCommonName(), connected ? HostList.PartialHostList.ChangeType.ADD : HostList.PartialHostList.ChangeType.REMOVE); // a jármálista változását tartalmazó üzenet azoknak, akik a járműválasztóban vannak
        ControllerData.BoolenPartialControllerData msgConn = new ControllerData.BoolenPartialControllerData(connected, ControllerData.BoolenPartialControllerData.BooleanType.CONNECTED); // kapcsolódás/lekapcsolódás jelző üzenet azoknak, akik a járműhöz tartoznak
        ControllerData.OfflineChangeablePartialControllerData msgOffDat = new ControllerData.OfflineChangeablePartialControllerData(new ControllerData.OfflineChangeableDatas(storage.getHostData().isFullX(), storage.getHostData().isFullY(), storage.getHostData().isVehicleConnected(), storage.getHostData().isUp2Date(), ControllerStorage.createHostState(storage))); // az offline megváltozhatott adatokat tartalmazó üzenet azoknak, akik a járműhöz tartoznak
        for (ControllerStorage cs : controllers) { // végigmegy az összes vezérlőn
            // csak azok számítanak, akiknek engedélyezve van a jármű
            if (Permissions.getConfig().isEnabled(getRemoteCommonName(), cs.getName())) {
                // ha nincs jármű kiválasztva, akkor járműválasztóban van, tehát járműlista változás küldése
                if (cs.getHostStorage() == null) {
                    cs.getMessageProcess().sendMessage(msgLs, false);
                }
                // ha a vezérlő az adott járműhöz tartozik, akkor offline módosulható adatok elküldése és jelzés arról, hogy elérhető-e a jármű
                if (cs.getHostStorage() == storage) {
                    cs.getMessageProcess().sendMessage(msgOffDat, false);
                    cs.getMessageProcess().sendMessage(msgConn, false);
                }
            }
        }
    }
    
    /**
     * Ha a jármű-kliens teljes adatot küld, akkor kapcsolódott a hídhoz,
     * ha részadatot küld, akkor a szenzoradatok változtak meg.
     * Kapcsolódáskor létrejön/frissül a jármű munkamenete és a vezérlő-kliensek figyelmeztetve lesznek a kapcsolódásról.
     * Részadat változásakor a jármű üzenetfogadója dolgozza fel az üzenetet.
     */
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
