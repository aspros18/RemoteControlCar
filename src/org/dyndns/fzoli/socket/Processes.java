package org.dyndns.fzoli.socket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.dyndns.fzoli.socket.process.Process;

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
    
}
