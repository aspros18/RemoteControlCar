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
        if (data instanceof HostList) {
            HOST_LIST.update((HostList) data);
            showHostSelectionFrame(HOST_LIST);
        }
        else if (data instanceof ControllerData) {
            //TODO: csak teszt
            Main.showConnectionStatus(ConnectionProgressFrame.Status.DISCONNECTED);
        }
    }
    
    /**
     * Részmodel alapján a model frissítése.
     */
    public static void update(PartialData data) {
        if (data instanceof HostList.PartialHostList) {
            ((HostList.PartialHostList) data).apply(HOST_LIST);
            showHostSelectionFrame(HOST_LIST);
        }
        else if (data instanceof PartialBaseData) {
            //TODO
        }
    }
    
    /**
     * Üzenet küldése a hídnak.
     * Ha nincs kialakítva üzenetküldésre alkalmas kapcsolat, nem küld üzenetet.
     */
    private static void sendMessage(Serializable msg) {
        if (msg == null) return;
        ControllerMessageProcess cmp = ClientProcesses.findProcess(ConnectionKeys.KEY_CONN_MESSAGE, ControllerMessageProcess.class);
        if (cmp != null) cmp.sendMessage(msg);
    }
    
}
