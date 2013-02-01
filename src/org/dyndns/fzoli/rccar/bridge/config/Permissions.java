package org.dyndns.fzoli.rccar.bridge.config;

import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.dyndns.fzoli.rccar.ConnectionKeys;
import org.dyndns.fzoli.rccar.bridge.Main;
import org.dyndns.fzoli.rccar.model.bridge.ControllerStorage;
import org.dyndns.fzoli.rccar.model.bridge.HostStorage;
import org.dyndns.fzoli.rccar.model.bridge.StorageList;
import org.dyndns.fzoli.rccar.model.controller.HostList;
import org.dyndns.fzoli.socket.ServerProcesses;
import org.dyndns.fzoli.socket.process.SecureProcess;

/**
* A híd jogosultságok adatbázisa.
* A jogosultság konfiguráció naprakészen tartása a feladata.
* Az osztály betöltésekor inicializálódik a konfiguráció.
* Ez után elindul egy időzítő, ami 5 másodpercenként megnézi, hogy módosult-e a konfiuráció és ha módosult, frissíti azt.
* Miután az új konfig életbe lépett, a már online vezérlőkön végigmegy és ha változott egy felhasználó jogosultsága, akkor elküldi az új
* jogosultságot a programnak, hogy frissítse azt a felületen. Ha egy felhasználótól megvonták a vezérlés jogát azon az autón, amit vezérel, a szerver azonnal elveszi tőle
* az autó vezérlését, mint ha lemondott volna róla a felhasználó, és ha a teljes jogot mevonják tőle, akkor visszakerül a járműválasztó menübe,
* mint ha a jármű kliens programja kilépett volna a szerverről (és a listájából kikerül a megvont jármű).
* Ha egy felhasználó jogot kapott egy jármű figyeléséhez, esetleg a vezérléséhez is és még nem választott járművet (tehát kap jelzést a lista módosulásáról),
* a listája frissítésre kerül, mint ha a jármű most csatlakozott volna a szerverhez.
* Ha a tiltólistára kerül fel egy online kliens, akkor a szerver természetesen bontja vele a kapcsolatot és legközelebbi kapcsolódáskor már nem fogja beenedni.
* @author zoli
*/
public final class Permissions {
    
    /**
     * Az aktuális konfiguráció.
     */
    private static final PermissionConfig CONFIG = new PermissionConfig();

    /**
     * Konstruktor.
     * Elindít egy időzítőt, ami 5 másodpercenként megnézi, módosult-e a konfiguráció és ha igen,
     * meghívja a frissítő metódust a régi konfigurációt átadva neki, hogy össze lehseen hasonlítani az eltéréseket.
     */
    static {
        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                PermissionConfig prev = CONFIG.refresh();
                if (prev != null) onRefresh(prev);
            }
            
        }, 0, 5000);
    }
    
    /**
     * Az osztályt nem kell példányosítani.
     */
    private Permissions() {
    }

    /**
     * Az aktuális konfigurációt adja vissza.
     */
    public static PermissionConfig getConfig() {
        return CONFIG;
    }
    
    /**
     * Ha a konfiguráció módosul, ez a metódus fut le.
     * TODO: ha a fehér listában módosul a sorrend, jelenlegi járművezérlő megtartása, de a többiek sorbarendezése
     * @param previous az előző konfiguráció
     */
    private static void onRefresh(PermissionConfig previous) {
        // végigmegy a kapcsolatokon és bezárja a kapcsolatokat azokkal a kliensekkel, akik ki lettek tiltva a hídról:
        Iterator<SecureProcess> it = ServerProcesses.getProcesses(SecureProcess.class).iterator();
        while (it.hasNext()) {
            SecureProcess proc = it.next();
            // ha már figyelembe lett véve a kliens, tovább urik a következő kapcsolatra
            if (proc.getConnectionId() != ConnectionKeys.KEY_CONN_DISCONNECT) continue;
            String name = proc.getRemoteCommonName(); // a kliens neve
            if (!previous.isBlocked(name) && getConfig().isBlocked(name)) proc.getHandler().closeProcesses(); // ha nem volt tiltva eddig, de most már tiltva van, akkor kapcsolatok bezárása a klienssel
            if (!Main.CONFIG.isStrict() || !proc.getDeviceId().equals(ConnectionKeys.KEY_DEV_CONTROLLER)) continue; // ha nem vezérlőé a kapcsolat vagy nem aktív a szigorú mód, tovább a következő kapcsolatra
            if (!Permissions.getConfig().isControllerWhite(name)) proc.getHandler().closeProcesses(); // ha már nem szerepel a fehér listában a vezérlő neve, akkor kapcsolatok bezárása a klienssel
        }
        
        // végigmegy a vezérlőkön és frissíti azokat, melyeket érint a konfiguráció módosulás:
        List<ControllerStorage> ls = StorageList.getControllerStorageList();
        for (ControllerStorage cs : ls) {
            if (cs.getMessageProcess().getSocket().isClosed()) continue; // kihagyja azokat, mely vezérlők kapcsolata bontva van
            if (cs.getHostStorage() != null && cs.getHostStorage().isConnected()) { // ha a vezérlőhöz tartozik jármű és az kapcsolódva van
                boolean viewOnly = getConfig().isViewOnly(cs.getHostStorage().getName(), cs.getName());
                if (previous.isViewOnly(cs.getHostStorage().getName(), cs.getName()) != viewOnly) { // ha változott a viewOnly paraméter
                    cs.getSender().setViewOnly(viewOnly); // közli a vezérlővel, hogy nem kérhet-e vezérlést ...
                    if (viewOnly && cs.getHostStorage().getOwner() == cs) cs.getReceiver().setWantControl(false); // ... és ha nem kérhet, de éppen vezérelte, elveszi tőle a vezérlést
                }
                boolean enabled = getConfig().isEnabled(cs.getHostStorage().getName(), cs.getName());
                if (!enabled && previous.isEnabled(cs.getHostStorage().getName(), cs.getName())) {
                    cs.getReceiver().setHostName(null);
                }
            }
            else { // ha a járműválasztóban van
                List<HostStorage> hosts = StorageList.getHostStorageList();
                for (HostStorage hs : hosts) { // az összes járművet megvizsgálja
                    if (!hs.isConnected()) continue; // ha offline a jármű, kihagyja
                    boolean enabled = getConfig().isEnabled(hs.getName(), cs.getName());
                    if (enabled && !previous.isEnabled(hs.getName(), cs.getName())) { // ha korábban tiltva volt, de már engedélyezve van a jármű
                        cs.getMessageProcess().sendMessage(new HostList.PartialHostList(hs.getName(), HostList.PartialHostList.ChangeType.ADD)); // üzenet küldése a vezérlőnek, hogy elérhető a jármű
                    }
                }
            }
        }
    }
    
}
