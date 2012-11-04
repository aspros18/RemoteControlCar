package org.dyndns.fzoli.rccar.controller;

import static org.dyndns.fzoli.rccar.controller.Main.showHostSelectionFrame;
import org.dyndns.fzoli.rccar.model.Data;
import org.dyndns.fzoli.rccar.model.PartialBaseData;
import org.dyndns.fzoli.rccar.model.PartialData;
import org.dyndns.fzoli.rccar.model.controller.ControllerData;
import org.dyndns.fzoli.rccar.model.controller.HostList;

/**
 *
 * @author zoli
 */
public class ControllerModels {
    
    /**
     * Járműlista.
     */
    private static final HostList HOST_LIST = new HostList();
    
    /**
     * Teljes model lecserélése.
     */
    public static void update(Data data) {
        if (data instanceof HostList) {
            HOST_LIST.update((HostList) data);
            showHostSelectionFrame(HOST_LIST);
        }
        else if (data instanceof ControllerData) {
            //TODO
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
    
}
