package org.dyndns.fzoli.rccar.bridge.config;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import org.dyndns.fzoli.rccar.ConnectionKeys;
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
    private static final PermissionConfig config = new PermissionConfig();

    /**
     * Konstruktor.
     * Elindít egy időzítőt, ami 5 másodpercenként megnézi, módosult-e a konfiguráció és ha igen,
     * meghívja a frissítő metódust a régi konfigurációt átadva neki, hogy össze lehseen hasonlítani az eltéréseket.
     */
    static {
        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                PermissionConfig prev = config.refresh();
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
        return config;
    }
    
    /**
     * Ha a konfiguráció módosul, ez a metódus fut le.
     * @param previous az előző konfiguráció
     */
    private static void onRefresh(PermissionConfig previous) {
        // végigmegy a kapcsolatokon és bezárja a kapcsolatokat azokkal a vezérlőkkel, akik ki lettek tiltva a hídról:
        Iterator<SecureProcess> it = ServerProcesses.getProcesses(SecureProcess.class).iterator();
        while (it.hasNext()) {
            SecureProcess proc = it.next();
            // ha nem vezérlő a kapcsolat, vagy már figyelembe lett véve a vezérlő, tovább urik a következő kapcsolatra
            if (proc.getDeviceId() != ConnectionKeys.KEY_DEV_CONTROLLER && proc.getConnectionId() != ConnectionKeys.KEY_CONN_DISCONNECT) continue;
            String controllerName = proc.getRemoteCommonName(); // a vezérlő neve
            if (!previous.isBlocked(controllerName) && getConfig().isBlocked(controllerName)) {
                proc.getHandler().closeProcesses(); // ha nem volt tiltva eddig, de most már tiltva van, akkor kapcsolatok bezárása a vezérlővel
            }
        }
    }
    
}
