package org.dyndns.fzoli.rccar.bridge.socket;

import java.io.EOFException;
import java.io.InvalidClassException;
import org.dyndns.fzoli.rccar.bridge.ConnectionAlert;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.impl.MessageProcess;

/**
 * A híd oldalán mindegyik üzenetfeldolgozó alapja.
 * @author zoli
 */
public abstract class BridgeMessageProcess extends MessageProcess {

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
            ConnectionAlert.log(getRemoteCommonName() + " túl régi kliens programot használ");
            getHandler().closeProcesses();
        }
        catch (EOFException e) {
            ; // nem érdekes hiba
        }
        catch (Exception e) {
            super.onException(e);
        }
    }
    
}
