package org.dyndns.fzoli.rccar.controller;

import java.io.Serializable;
import org.dyndns.fzoli.rccar.ConnectionKeys;
import static org.dyndns.fzoli.rccar.controller.Main.showHostSelectionFrame;
import org.dyndns.fzoli.rccar.controller.socket.ControllerMessageProcess;
import org.dyndns.fzoli.rccar.model.Data;
import org.dyndns.fzoli.rccar.model.PartialBaseData;
import org.dyndns.fzoli.rccar.model.PartialData;
import org.dyndns.fzoli.rccar.model.controller.ControllerData;
import org.dyndns.fzoli.rccar.model.controller.HostList;
import org.dyndns.fzoli.socket.ClientProcesses;

/**
 * A vezérlő oldalán tárolja az adatokat és üzenhet a hídnak.
 * @author zoli
 */
public class ControllerModels {
    
    /**
     * Járműlista.
     */
    private static final HostList HOST_LIST = new HostList();
    
    /**
     * A kiválasztott jármű adatai.
     * TODO: adat megváltozás esetén üzenetküldés és public helyett private legyen a sendMessage
     */
    private static final ControllerData DATA = new ControllerData() {

        /**
         * Beállítja az aktuális jármű nevét és üzen a hídnak.
         */
        @Override
        public void setHostName(String hostName) {
            super.setHostName(hostName);
            sendMessage(new ControllerData.HostNamePartialControllerData(hostName));
        }
        
    };

    /**
     * A kiválasztott jármű adatai.
     */
    public static ControllerData getData() {
        return DATA;
    }
    
    /**
     * Teljes model lecserélése.
     */
    public static void update(Data data) {
        if (data instanceof HostList) { // ha teljes járműlista érkezett
            HOST_LIST.update((HostList) data); // járműlista frissítése
            showHostSelectionFrame(HOST_LIST); // felület frissítése
        }
        else if (data instanceof ControllerData) { // ha teljes járműadat érkezett
            HOST_LIST.getHosts().clear(); // járműlista kiürítése, memória felszabadítás
            Main.showConnectionStatus(ConnectionProgressFrame.Status.DISCONNECTED); // TODO: csak teszt
        }
    }
    
    /**
     * Részmodel alapján a model frissítése.
     */
    public static void update(PartialData data) {
        if (data instanceof HostList.PartialHostList) { // ha a járműlista változott
            ((HostList.PartialHostList) data).apply(HOST_LIST); // járműlista frissítése
            showHostSelectionFrame(HOST_LIST); // felület frissítése
        }
        else if (data instanceof PartialBaseData) { // ha a kiválasztott jármű adata változott meg
            //TODO
        }
    }
    
    /**
     * Üzenet küldése a hídnak.
     * Ha nincs kialakítva üzenetküldésre alkalmas kapcsolat, nem küld üzenetet.
     */
    private static void sendMessage(Serializable msg) {
        if (msg == null) return; // nincs mit küldeni
        ControllerMessageProcess cmp = ClientProcesses.findProcess(ConnectionKeys.KEY_CONN_MESSAGE, ControllerMessageProcess.class); // üzenetküldő referencia megszerzése
        if (cmp != null) cmp.sendMessage(msg); // ha van mivel küldeni, küldés
    }
    
}
