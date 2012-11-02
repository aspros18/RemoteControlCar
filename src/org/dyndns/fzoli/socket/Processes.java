package org.dyndns.fzoli.socket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.dyndns.fzoli.socket.process.Process;
import org.dyndns.fzoli.socket.process.SecureProcess;

/**
 * Az aktív kapcsolatfeldolgozókat tároló osztályok őse.
 * @author zoli
 */
public class Processes {
    
    /**
     * Szálbiztos listát készít.
     */
    protected static List<Process> createList() {
        return Collections.synchronizedList(new ArrayList<Process>());
    }
    
    /**
     * A paraméterben átadott listát leszűri.
     * @param processes a megszűrendő lista
     * @param clazz a szűrőfeltétel
     */
    public static <T extends Process> List<T> getProcesses(List<Process> processes, Class<T> clazz) {
        List<T> ls = Collections.synchronizedList(new ArrayList<T>());
        for (Process proc : processes) {
            try {
                ls.add((T) proc);
            }
            catch (ClassCastException ex) {
                ;
            }
        }
        return ls;
    }
    
    /**
     * Bezárja az összes kapcsolatot, melyre illik a szűrő feltétel.
     * @param procs a kapcsolatokat tartalmazó lista
     * @param deviceId szűrőfeltétel
     * @param remoteCommonName szűrőfeltétel
     */
    public static void closeProcesses(List<SecureProcess> procs, int deviceId, String remoteCommonName) {
        for (SecureProcess prc : procs) { // végigmegy a biztonságos kapcsolatfeldolgozókon ...
            try {
                // ... és ha megegyező eszközazonosítóval és Common Name mezővel rendelkeznek ...
                if (prc.getDeviceId().equals(deviceId) && prc.getRemoteCommonName().equals(remoteCommonName)) {
                    prc.getSocket().close(); // ... bezárja a kapcsolatukat
                }
            }
            catch (Exception ex) { // ha nem sikerült bezárni a socketet, akkor már zárva volt
                ;
            }
        }
    }
    
}
