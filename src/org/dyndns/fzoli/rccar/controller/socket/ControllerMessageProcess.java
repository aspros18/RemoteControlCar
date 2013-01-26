package org.dyndns.fzoli.rccar.controller.socket;

import java.io.InvalidClassException;
import java.io.Serializable;
import org.dyndns.fzoli.rccar.controller.ControllerModels;
import org.dyndns.fzoli.rccar.controller.resource.R;
import org.dyndns.fzoli.rccar.controller.view.ChatDialog;
import org.dyndns.fzoli.rccar.model.Data;
import org.dyndns.fzoli.rccar.model.PartialData;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.impl.MessageProcess;
import static org.dyndns.fzoli.ui.UIUtil.alert;

/**
 * A vezérlő üzenetküldő és üzenetfogadó osztálya.
 * @author zoli
 */
public class ControllerMessageProcess extends MessageProcess {

    public ControllerMessageProcess(SecureHandler handler) {
        super(handler);
    }

    /**
     * Ha a kapcsolat létrejött, a chatablakban a saját felhasználónév beállítása.
     */
    @Override
    protected void onStart() {
        ChatDialog.setSenderName(getLocalCommonName());
    }

    /**
     * Kivétel keletkezett az egyik üzenet elküldésekor / inicializálás közben / megszakadt a kapcsolat.
     */
    @Override
    protected void onException(Exception ex) {
        try {
            throw ex;
        }
        catch (InvalidClassException e) {
            alert("Hiba!", "A kliens program nem kompatibilis a szerver verziójával.", System.err, R.getIconImage());
            System.exit(1);
        }
        catch (Exception e) {
            ; // a többi hiba nem érdekes
        }
    }

    /**
     * Az üzenet feldolgozását a ControllerModels végzi.
     */
    @Override
    protected void onMessage(Serializable o) {
        if (o instanceof Data) {
            ControllerModels.update((Data) o);
        }
        else if (o instanceof PartialData) {
            ControllerModels.update((PartialData) o);
        }
    }
    
}
