package org.dyndns.fzoli.rccar.controller;

import java.io.Serializable;
import org.dyndns.fzoli.rccar.ConnectionKeys;
import static org.dyndns.fzoli.rccar.controller.Main.showControllerWindows;
import static org.dyndns.fzoli.rccar.controller.Main.showHostSelectionFrame;
import org.dyndns.fzoli.rccar.controller.socket.ControllerMessageProcess;
import org.dyndns.fzoli.rccar.model.Data;
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
     * Egy konkrét jármű adatait tartalmazó tároló, valamint üzenetküldő támogatás kliens oldalra.
     */
    public static class ClientControllerData extends ControllerData {

        /**
         * Részadatküldő implementáció kliens oldalra.
         * @see ControllerDataSender
         */
        private static class ClientControllerDataSender extends ControllerData.ControllerDataSender {

            /**
             * Konstruktor.
             * Az eszközazonosítóra nincs szükség, mert az adatmódosulást vagy a
             * vezérlő program okozza helyben (a felhasználó kérésére) vagy
             * a híd okozza azt (a szerver oldalán módosult az adat).
             * Mivel a kliens oldalán nincs olyan, hogy a szerver üzenetére válaszüzenet
             * kerül küldésre, ezért a küldő nevére sincs szükség, mivel csak akkor kerül
             * felhasználásra ez az osztály, ha tényleg üzenetet kell küldeni a szervernek,
             * egyébként egyszerűen az eredeti adatmodel setter metódusa kerül felhasználásra,
             * ami nem küld üzenetet a szervernek soha, csak helyben módosítja az adatokat.
             * @param data a helyi adatmodel, amin a setter metódusok alkalmazódnak
             */
            public ClientControllerDataSender(ControllerData data) {
                super(data);
            }

            /**
             * Üzenet küldése a hídnak.
             * Ha nincs kialakítva üzenetküldésre alkalmas kapcsolat, nem küld üzenetet.
             */
            @Override
            protected void sendMessage(Serializable msg) {
                if (msg == null) return; // nincs mit küldeni
                ControllerMessageProcess cmp = ClientProcesses.findProcess(ConnectionKeys.KEY_CONN_MESSAGE, ControllerMessageProcess.class); // üzenetküldő referencia megszerzése
                if (cmp != null) cmp.sendMessage(msg); // ha van mivel küldeni, küldés
            }

        }
        
        /**
         * A kliens oldal üzenetküldője.
         */
        private final ControllerData sender = new ClientControllerData.ClientControllerDataSender(this);
        
        /**
         * A kliens oldali tároló konstruktora.
         */
        public ClientControllerData() {
            super();
        }

        /**
         * A kliens oldal üzenetküldője.
         * A setter metódusai a híd szervernek küldik az üzeneteket.
         */
        public ControllerData getSender() {
            return sender;
        }
        
    }
    
    /**
     * Járműlista.
     */
    private static final HostList HOST_LIST = new HostList();
    
    /**
     * A kiválasztott jármű adatait tartalmazó tároló üzenetküldő támogatással.
     */
    private static final ClientControllerData DATA = new ClientControllerData();
    
    /**
     * A kiválasztott jármű adatai.
     */
    public static ClientControllerData getData() {
        return DATA;
    }
    
    /**
     * Teljes model lecserélése.
     */
    public static void update(Data data) {
        if (data instanceof HostList) { // ha teljes járműlista érkezett
            HOST_LIST.update((HostList) data); // járműlista frissítése
            DATA.clear(); // adatmodell kiürítése, memória felszabadítás
            showHostSelectionFrame(HOST_LIST); // felület frissítése
        }
        else if (data instanceof ControllerData) { // ha teljes járműadat érkezett
            DATA.update((ControllerData) data); // adatmodell frissítése
            HOST_LIST.clear(); // járműlista kiürítése, memória felszabadítás
            showControllerWindows(); // járművel kapcsoltos ablakok megjelenítése
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
        else if (data instanceof ControllerData.PartialControllerData) { // ha a kiválasztott jármű adata változott meg
            //TODO
        }
    }
    
}
