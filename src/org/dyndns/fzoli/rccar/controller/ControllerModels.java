package org.dyndns.fzoli.rccar.controller;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import org.dyndns.fzoli.rccar.ConnectionKeys;
import static org.dyndns.fzoli.rccar.controller.Main.showControllerWindows;
import static org.dyndns.fzoli.rccar.controller.Main.showHostSelectionFrame;
import org.dyndns.fzoli.rccar.controller.socket.ControllerMessageProcess;
import org.dyndns.fzoli.rccar.controller.view.ArrowDialog;
import org.dyndns.fzoli.rccar.controller.view.ChatDialog;
import org.dyndns.fzoli.rccar.controller.view.ControllerFrame;
import org.dyndns.fzoli.rccar.controller.view.map.MapDialog;
import org.dyndns.fzoli.rccar.model.Control;
import org.dyndns.fzoli.rccar.model.Data;
import org.dyndns.fzoli.rccar.model.PartialBaseData;
import org.dyndns.fzoli.rccar.model.PartialData;
import org.dyndns.fzoli.rccar.model.controller.ChatMessage;
import org.dyndns.fzoli.rccar.model.controller.ControllerData;
import org.dyndns.fzoli.rccar.model.controller.ControllerState;
import org.dyndns.fzoli.rccar.model.controller.ForwardedList;
import org.dyndns.fzoli.rccar.model.controller.HostList;
import org.dyndns.fzoli.rccar.model.controller.HostState;
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
         * A felületfrissítések implementálása a leszármazott osztályokban történnek meg.
         */
        private static class RefreshList<T> extends ForwardedList<T> {

            /**
             * A model, amitől elkérhető a chat dialógus referenciája.
             */
            private final ClientControllerData d;
            
            /**
             * Konstruktor.
             * @param d a model, amitől elkérhető a chat dialógus referenciája
             * @param l az eredeti lista, ami szintén frissül
             */
            public RefreshList(ClientControllerData d, List<T> l) {
                super(l);
                this.d = d;
            }
            
            /**
             * A chat dialógus referenciája.
             */
            protected ChatDialog getChatDialog() {
                return d.dialogChat;
            }

        }
        
        /**
         * Chatüzenet frissítő.
         * Mivel nem lehet üzenetet törölni, a {@code remove} metódus nincs implementálva.
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

            /**
             * A chatüzenetek hozzáadása a felülethez a kollekcióban szereplő sorrendben és a helyi adatmodel frissítése.
             */
            @Override
            public boolean addAll(Collection<? extends ChatMessage> c) {
                boolean res = super.addAll(c);
                if (getChatDialog() != null) getChatDialog().addChatMessages(c);
                return res;
            }

            /**
             * A chatüzenetek törlése a felületről és a helyi adatmodel frissítése.
             */
            @Override
            public void clear() {
                super.clear();
                if (getChatDialog() != null) getChatDialog().removeChatMessages();
            }
            
        }
        
        /**
         * Vezérlőlista frissítő.
         */
        private static class ControllerRefreshList extends RefreshList<ControllerState> {

            /**
             * Konstruktor.
             * @param d a model, amitől elkérhető a chat dialógus referenciája
             * @param l az eredeti lista, ami szintén frissül
             */
            public ControllerRefreshList(ClientControllerData d, List<ControllerState> l) {
                super(d, l);
            }

            /**
             * Vezérlő-lista frissítése.
             * @param name a vezérlő neve
             * @param add hozzáadás vagy eltávolítás
             */
            private void setController(ControllerState s, boolean add) {
                if (getChatDialog() != null) getChatDialog().setControllerVisible(s, add, true);
            }
            
            /**
             * A kapcsolódott vezérlő megjelenítése a felületen és a helyi adatmodel frissítése.
             */
            @Override
            public boolean add(ControllerState e) {
                setController(e, true);
                return super.add(e);
            }

            /**
             * A lekapcsolódott vezérlő eltávolítása a felületről és a helyi adatmodel frissítése.
             */
            @Override
            public boolean remove(Object o) {
                if (o instanceof ControllerState) setController((ControllerState) o, false);
                return super.remove(o);
            }

            /**
             * A vezérlők hozzáadása a felülethez a kollekcióban szereplő sorrendben és a helyi adatmodel frissítése.
             */
            @Override
            public boolean addAll(Collection<? extends ControllerState> c) {
                boolean res = super.addAll(c);
                if (getChatDialog() != null) getChatDialog().addControllers(c);
                return res;
            }

            /**
             * A vezérlők törlése a felületről és a helyi adatmodel frissítése.
             */
            @Override
            public void clear() {
                super.clear();
                if (getChatDialog() != null) getChatDialog().removeControllers();
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
         * A vezérlő dialógus referenciája.
         */
        private ArrowDialog dialogArrow;
        
        /**
         * A térkép dialógus referenciája.
         */
        private MapDialog dialogMap;
        
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
         * A vezérlő dialógus referenciájának átadása, hogy a setterek frissíteni tudják a felületet.
         */
        public void setArrowDialog(ArrowDialog dialogArrow) {
            if (dialogArrow != null) this.dialogArrow = dialogArrow;
        }
        
        /**
         * A térkép dialógus referenciájának átadása, hogy a setterek frissíteni tudják a felületet.
         */
        public void setMapDialog(MapDialog dialogMap) {
            if (dialogMap != null) this.dialogMap = dialogMap;
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
         * Akkor érhető el a jármű, a kapcsolatokban nincs időtúllépés és a telefonhoz hozzá van kötve az IOIO.
         * @param conn ha false, akkor nem vizsgálja az IOIO-val a kapcsolatot
         * @param online ha false, akkor nem vizsgálja az offline paramétert a modelben
         */
        public boolean isVehicleAvailable(boolean conn, boolean online) {
            return !isUnderTimeout() && isHostUnderTimeout() != null && !isHostUnderTimeout() && (conn ? isVehicleConnected() != null && isVehicleConnected() : true) && (online ? isConnected() != null && isConnected() : true);
        }

        /**
         * A kiválasztott hoszthoz tartozó chatüzenetek listáját adja meg.
         * A lista {@code add} metódusa frissíti a felületet.
         */
        @Override
        public List<ChatMessage> getChatMessages() {
            return refChat;
        }

        /**
         * A kiválasztott hoszthoz tartozó vezérlők listáját adja vissza.
         * A lista {@code add} és {@code remove} metódusa frissíti a felületet.
         */
        @Override
        public List<ControllerState> getControllers() {
            return refController;
        }

        /**
         * Beállítja, hogy pontosan szabályozható-e a az irány.
         * Az adat módosulása után beállítódik a vezérlő dialógus.
         */
        @Override
        public void setFullX(Boolean fullX) {
            super.setFullX(fullX);
            if (dialogArrow != null) dialogArrow.refreshFullXY();
        }

        /**
         * Beállítja, hogy pontosan szabályozható-e a a sebesség.
         * Az adat módosulása után beállítódik a vezérlő dialógus.
         */
        @Override
        public void setFullY(Boolean fullY) {
            super.setFullY(fullY);
            if (dialogArrow != null) dialogArrow.refreshFullXY();
        }
        
        /**
         * Beállítja, hogy a Híddal kiépített kapcsolatban időtúllépés van-e.
         * Az adat módosulása után frissül a főablak, a vezérlő dialógus és a térkép dialógus egy része.
         */
        public void setUnderTimeout(boolean underTimeout) {
            this.underTimeout = underTimeout;
            refreshConnectionStatus();
        }
        
        /**
         * Beállítja az akkumulátorszintet.
         * Az adat módosulása után frissül a főablak egy része.
         */
        @Override
        public void setBatteryLevel(Integer batteryLevel) {
            super.setBatteryLevel(batteryLevel);
            if (frameMain != null) frameMain.refreshBattery();
        }

        /**
         * A jármű vezérlőjelét állítja be.
         * Az adat módosulása után frissül a főablak és a vezérlő dialógus egy része.
         */
        @Override
        public void setControl(Control control) {
            super.setControl(control);
            if (frameMain != null) frameMain.refreshBattery();
            if (dialogArrow != null) dialogArrow.refreshControl();
        }

        /**
         * Beállítja azt, hogy a jármű vezérlése elérhető-e.
         * Az adat módosulása után frissül a főablak és a vezérlő dialógus egy része.
         */
        @Override
        public void setControlling(Boolean controlling) {
            super.setControlling(controlling);
            if (frameMain != null) frameMain.refreshControllButton(null);
            if (dialogArrow != null) dialogArrow.refreshControlling();
        }

        /**
         * Beállítja a jármű pillanatnyi állapotát.
         * Az adat módosulása után frissül a főablak és a térkép dialógus egy része.
         * @param hostState a jármű pillanatnyi állapota
         */
        @Override
        public void setHostState(HostState hostState) {
            super.setHostState(hostState);
            if (frameMain != null) {
                frameMain.refreshSpeed();
            }
            if (dialogMap != null) {
                dialogMap.refreshArrow();
                dialogMap.refreshPosition();
            }
        }

        /**
         * Beállítja azt, hogy a jármű kapssolata időtúllépés alatt van-e.
         * Az adat módosulása után frissül a főablak, a vezérlő dialógus és a térkép dialógus egy része.
         */
        @Override
        public void setHostUnderTimeout(Boolean hostConnected) {
            super.setHostUnderTimeout(hostConnected);
            refreshConnectionStatus();
        }

        /**
         * Beállítja, hogy a GPS adat naprakész-e.
         * Az adat módosulása után frissül a főablak és a térkép dialógus egy része.
         */
        @Override
        public void setUp2Date(Boolean up2date) {
            super.setUp2Date(up2date);
            if (frameMain != null) frameMain.refreshSpeed();
            if (dialogMap != null) dialogMap.refreshFade();
        }

        /**
         * Beállítja azt, hogy a jármű kapcsolódva van-e a Hídhoz.
         * Az adat módosulása után frissül a főablak, a vezérlő dialógus és a térkép dialógus egy része.
         */
        @Override
        public void setConnected(Boolean connected) {
            super.setConnected(connected);
            refreshConnectionStatus();
        }

        /**
         * Beállítja azt, hogy a jármű kapcsolódva van-e a telefonhoz.
         * Az adat módosulása után frissül a főablak, a vezérlő dialógus és a térkép dialógus egy része.
         */
        @Override
        public void setVehicleConnected(Boolean vehicleConnected) {
            super.setVehicleConnected(vehicleConnected);
            refreshConnectionStatus();
        }

        /**
         * Ha a kapcsolat státusza megváltozik,
         * frissül a főablak, a vezérlő dialógus és a térkép dialógus egy része.
         */
        private void refreshConnectionStatus() {
            if (frameMain != null) {
                frameMain.refreshBattery();
                frameMain.refreshMessage();
                frameMain.refreshSpeed();
            }
            if (dialogArrow != null) {
                dialogArrow.refreshControlling();
            }
            if (dialogMap != null) {
                dialogMap.refreshFade();
            }
        }
        
        /**
         * Beállítja azt, hogy a jármű irányítását lehet-e kérni.
         * Az adat módosulása után frissül a főablak egy része.
         * @param viewOnly true esetén nem kérhető az irányítás soha
         */
        @Override
        public void setViewOnly(Boolean viewOnly) {
            super.setViewOnly(viewOnly);
            if (frameMain != null) frameMain.refreshControllButton(null);
        }
        
        /**
         * Beállítja azt, hogy szeretné-e a felhasználó vezérelni az autót.
         * Az adat módosulása után frissül a főablak egy része.
         */
        @Override
        public void setWantControl(Boolean wantControl) {
            Boolean prev = getData().isWantControl();
            super.setWantControl(wantControl);
            if (frameMain != null) frameMain.refreshControllButton(prev);
        }

        /**
         * Ha egy vezérlő állapota megváltozik, a felületet frissíteni kell.
         * A chat dialógus listájából kikerül a régi és hozzáadódik az új állapot.
         * Mivel ABC sorrendben frissül a lista, így a pozíciója nem módosul a vezérlőnek a listában.
         */
        @Override
        public void onControllerStateChanged(ControllerState cs) {
            super.onControllerStateChanged(cs);
            if (dialogChat != null) {
                dialogChat.setControllerVisible(cs, false, false);
                dialogChat.setControllerVisible(cs, true, false);
                if (cs.isControlling()) {
                    dialogChat.showNewController(cs.getName());
                }
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
