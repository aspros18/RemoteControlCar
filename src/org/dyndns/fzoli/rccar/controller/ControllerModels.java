package org.dyndns.fzoli.rccar.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.dyndns.fzoli.rccar.ConnectionKeys;
import static org.dyndns.fzoli.rccar.controller.Main.showControllerWindows;
import static org.dyndns.fzoli.rccar.controller.Main.showHostSelectionFrame;
import org.dyndns.fzoli.rccar.controller.socket.ControllerMessageProcess;
import org.dyndns.fzoli.rccar.controller.view.ChatDialog;
import org.dyndns.fzoli.rccar.controller.view.ControllerFrame;
import org.dyndns.fzoli.rccar.model.Data;
import org.dyndns.fzoli.rccar.model.PartialBaseData;
import org.dyndns.fzoli.rccar.model.PartialData;
import org.dyndns.fzoli.rccar.model.controller.ChatMessage;
import org.dyndns.fzoli.rccar.model.controller.ControllerData;
import org.dyndns.fzoli.rccar.model.controller.HostList;
import org.dyndns.fzoli.socket.ClientProcesses;

/**
 * A vezérlő oldalán tárolja az adatokat és üzenhet a hídnak.
 * @author zoli
 */
public class ControllerModels {
    
    /**
     * Egy konkrét jármű adatait tartalmazó tároló, valamint üzenetküldő támogatás kliens oldalra és GUI frissítés.
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
                setAddEnabled(false); // az üzenetek küldésekor a helyben tárolt lista módosítatlan marad, elkerülve az üzenet duplázódást a küldő oldalán
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
         * GUI frissítő lista.
         * Az {@code add} és {@code remove} metódusok implementálása az ősben történik meg.
         */
        private static class RefreshList<T> extends ArrayList<T> {

            /**
             * A model, amitől elkérhető a chat dialógus referenciája.
             */
            private final ClientControllerData d;
            
            /**
             * Az eredeti lista, ami szintén frissül.
             */
            private final List<T> l;
            
            /**
             * Konstruktor.
             * @param d a model, amitől elkérhető a chat dialógus referenciája
             * @param l az eredeti lista, ami szintén frissül
             */
            public RefreshList(ClientControllerData d, List<T> l) {
                this.d = d;
                this.l = l;
            }
            
            /**
             * A chat dialógus referenciája.
             */
            protected ChatDialog getChatDialog() {
                return d.dialogChat;
            }
            
            /**
             * Az eredeti lista frissítése.
             */
            @Override
            public boolean add(T e) {
                return l.add(e);
            }

            /**
             * Az eredeti lista frissítése.
             */
            @Override
            public boolean remove(Object o) {
                return l.remove(o);
            }
            
        }
        
        /**
         * Chatüzenet frissítő.
         * Mivel nem lehet üzenetet törölni, csak az {@code add} metódus van implementálva.
         */
        private static class ChatRefreshList extends RefreshList<ChatMessage> {

            /**
             * Konstruktor.
             * @param d a model, amitől elkérhető a chat dialógus referenciája
             * @param l az eredeti lista, ami szintén frissül
             */
            public ChatRefreshList(ClientControllerData d, List<ChatMessage> l) {
                super(d, l);
            }

            /**
             * A chatüzenet megjelenítése a felületen és a helyi adatmodel frissítése.
             */
            @Override
            public boolean add(ChatMessage e) {
                if (getChatDialog() != null) getChatDialog().addMessage(e.getDate(), e.getSender(), e.data);
                return super.add(e);
            }
            
        }
        
        /**
         * Vezérlőlista frissítő.
         * Az {@code add} és {@code remove} metódusok implementációja.
         */
        private static class ControllerRefreshList extends RefreshList<String> {

            /**
             * Konstruktor.
             * @param d a model, amitől elkérhető a chat dialógus referenciája
             * @param l az eredeti lista, ami szintén frissül
             */
            public ControllerRefreshList(ClientControllerData d, List<String> l) {
                super(d, l);
            }

            /**
             * Vezérlő-lista frissítése.
             * @param name a vezérlő neve
             * @param add hozzáadás vagy eltávolítás
             */
            private void setController(String name, boolean add) {
                if (getChatDialog() != null) getChatDialog().setControllerVisible(name, add, true);
            }
            
            /**
             * A kapcsolódott vezérlő megjelenítése a felületen és a helyi adatmodel frissítése.
             */
            @Override
            public boolean add(String e) {
                setController(e, true);
                return super.add(e);
            }

            /**
             * A lekapcsolódott vezérlő eltávolítása a felületről és a helyi adatmodel frissítése.
             */
            @Override
            public boolean remove(Object o) {
                setController(o.toString(), false);
                return super.remove(o);
            }
            
        }
        
        /**
         * A kliens oldal üzenetküldője.
         */
        private final ControllerData sender;
        
        /**
         * Az eredeti chatüzenet-lista GUI frissítéssel kibővítve.
         */
        private final ChatRefreshList refChat;
        
        /**
         * Az eredeti vezérlő-lista GUI frissítéssel kibővítve.
         */
        private final ControllerRefreshList refController;
        
        /**
         * A főablak referenciája.
         */
        private ControllerFrame frameMain;
        
        /**
         * A chat dialógus referenciája.
         */
        private ChatDialog dialogChat;
        
        /**
         * A Híddal kiépített kapcsolatban időtúllépés van-e.
         */
        private boolean underTimeout = false;
        
        /**
         * A kliens oldali tároló konstruktora.
         */
        public ClientControllerData() {
            super();
            refChat = new ChatRefreshList(this, super.getChatMessages());
            refController = new ControllerRefreshList(this, super.getControllers());
            sender = new ClientControllerData.ClientControllerDataSender(this);
        }

        /**
         * A chat dialógus referenciájának átadása, hogy a GUI frissítő chat lista frissíthesse a felületet.
         */
        public void setChatDialog(ChatDialog dialogChat) {
            if (dialogChat != null) this.dialogChat = dialogChat;
        }
        
        /**
         *  A főablak referenciájának átadása, hogy a setterek frissíteni tudják a felületet.
         */
        public void setControllerFrame(ControllerFrame frameMain) {
            if (frameMain != null) this.frameMain = frameMain;
        }
        
        /**
         * A kliens oldal üzenetküldője.
         * A setter metódusai a híd szervernek küldik az üzeneteket.
         */
        public ControllerData getSender() {
            return sender;
        }
        
        /**
         * Megadja, hogy a Híddal kiépített kapcsolatban időtúllépés van-e.
         */
        public boolean isUnderTimeout() {
            return underTimeout;
        }
        
        /**
         * Megmondja, hogy a jármű eérhető-e.
         * Akkor érhető el a jármű, ha a kapcsolatokban nincs időtúllépés és a telefonhoz hozzá van kötve az IOIO.
         */
        public boolean isVehicleAvailable() {
            return !isUnderTimeout() && isHostUnderTimeout() != null && !isHostUnderTimeout() && isVehicleConnected() != null && isVehicleConnected();
        }
        
        /**
         * Beállítja, hogy a Híddal kiépített kapcsolatban időtúllépés van-e.
         */
        public void setUnderTimeout(boolean underTimeout) {
            this.underTimeout = underTimeout;
            if (frameMain != null) {
                frameMain.refreshSpeed();
                frameMain.refreshBattery();
                frameMain.refreshMessage();
            }
        }

        @Override
        public List<ChatMessage> getChatMessages() {
            return refChat;
        }

        @Override
        public List<String> getControllers() {
            return refController;
        }
        
        @Override
        public void setWantControl(Boolean wantControl) {
            super.setWantControl(wantControl);
            if (frameMain != null) {
                frameMain.refreshControllButton();
            }
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
        else if (data instanceof PartialBaseData) { // ha a kiválasztott jármű adata változott meg
            ((PartialBaseData<ControllerData, ?>) data).apply(DATA); // adatmodel frissítése, mely setterei frissítik a felületet is
        }
    }
    
}
