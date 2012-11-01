package org.dyndns.fzoli.socket;

import java.util.List;
import org.dyndns.fzoli.socket.process.Process;
import org.dyndns.fzoli.socket.process.SecureProcess;

/**
 * Az aktív kapcsolatfeldolgozókat tároló osztály szerver oldalra.
 * @author zoli
 */
public class ServerProcesses extends Processes {
    
    /**
     * Aktív kapcsolatfeldolgozók.
     */
    private static final List<Process> PROCESSES = createList();
    
    /**
     * Azokat az adatfeldolgozókat adja vissza, melyek még dolgoznak.
     */
    public static List<Process> getProcesses() {
        return PROCESSES;
    }
    
    /**
     * A paraméterben átadott listát leszűri.
     * @param clazz a szűrőfeltétel
     */
    public static <T extends Process> List<T> getProcesses(Class<T> clazz) {
        return getProcesses(getProcesses(), clazz);
    }
    
    /**
     * Megkeresi az adatfeldolgozót a paraméterek alapján.
     * @param remoteName tanúsítvány common name
     * @param deviceId eszközazonosító
     * @param connectionId kapcsolatazonosító
     * @return null, ha nincs találat, egyébként adatfeldolgozó objektum
     */
    public static SecureProcess findProcess(String remoteName, int deviceId, int connectionId) {
        return findProcess(remoteName, deviceId, connectionId, SecureProcess.class);
    }
    
    /**
     * Megkeresi az adatfeldolgozót a paraméterek alapján.
     * @param remoteName tanúsítvány common name
     * @param deviceId eszközazonosító
     * @param connectionId kapcsolatazonosító
     * @param clazz az adatfeldolgozó típusa
     * @return null, ha nincs találat, egyébként adatfeldolgozó objektum
     */
    public static <T extends SecureProcess> T findProcess(String remoteName, int deviceId, int connectionId, Class<T> clazz) {
        List<Process> procs = getProcesses();
        for (Process proc : procs) {
            try {
                if (((SecureProcess) proc).getHandler().isCertEqual(remoteName, deviceId, connectionId)) return (T) proc;
            }
            catch (ClassCastException ex) {
                ;
            }
        }
        return null;
    }
    
}
