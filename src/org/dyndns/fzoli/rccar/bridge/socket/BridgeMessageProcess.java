package org.dyndns.fzoli.rccar.bridge.socket;

import java.io.InvalidClassException;
import org.dyndns.fzoli.rccar.bridge.ConnectionAlert;
import static org.dyndns.fzoli.rccar.bridge.Main.getString;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.impl.MessageProcess;

/**
 * A híd oldalán mindegyik üzenetfeldolgozó alapja.
 * @author zoli
 */
abstract class BridgeMessageProcess extends MessageProcess {

    /**
     * Biztonságos üzenetváltásra képes adatfeldolgozó inicializálása.
     * @param handler Biztonságos kapcsolatfeldolgozó, ami létrehozza ezt az adatfeldolgozót.
     * @throws NullPointerException ha handler null
     */
    public BridgeMessageProcess(SecureHandler handler) {
        super(handler);
    }

    /**
     * Kivétel keletkezett az egyik üzenet elküldésekor / inicializálás közben / megszakadt a kapcsolat.
     * Ha eltérő az osztályok verziója, figyelmeztetés és az adott klienssel a kapcsolatok megszakítása.
     */
    @Override
    protected void onException(Exception ex) {
        try {
            throw ex;
        }
        catch (InvalidClassException e) {
            ConnectionAlert.log(getRemoteCommonName() + ' ' + getString("warn_wrong_version")); // nem megfelelő kliens program verzió
            getHandler().closeProcesses();
        }
        catch (Exception e) {
            ; // a többi hiba nem érdekes
        }
    }
    
}
