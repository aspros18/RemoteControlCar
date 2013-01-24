package org.dyndns.fzoli.rccar.controller.socket;

import org.dyndns.fzoli.rccar.ConnectionKeys;
import org.dyndns.fzoli.rccar.controller.ControllerModels;
import static org.dyndns.fzoli.rccar.controller.Main.showConnectionStatus;
import org.dyndns.fzoli.rccar.controller.view.ConnectionProgressFrame.Status;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.impl.ClientDisconnectProcess;

/**
 * A vezérlő oldalán vizsgálja, hogy él-e még a kapcsolat a klienssel.
 * @author zoli
 */
public class ControllerDisconnectProcess extends ClientDisconnectProcess implements ConnectionKeys {
    
    /**
     * Vezérlő kliens oldalra időtúllépés detektáló.
     * 1 s és 10 s az időkorlát, 250 ms időközzel (meg kell egyezniük pontosan a szerverével).
     * @param handler Biztonságos kapcsolatfeldolgozó, ami létrehozza ezt az adatfeldolgozót.
     * @throws NullPointerException ha handler null
     */
    public ControllerDisconnectProcess(SecureHandler handler) {
        super(handler, DC_TIMEOUT1, DC_TIMEOUT2, DC_DELAY); // 1 és 10 mp időtúllépés, 250 ms sleep
    }

    /**
     * Beállítja a modelben az időtúllépés értékét, ezzel frissítve a felületet is.
     */
    private void setTimeout(boolean b) {
        ControllerModels.getData().setUnderTimeout(b);
    }

    /**
     * Ha a kapcsolat létrejött, az időtúllépés értéke hamisra állítódik be,
     * mert előforulhat, hogy időtúllépés után megszakad a kapcsolat, és újra kapcsolódás
     * után hívódik meg ez a metódus, amikoris a paraméter igaz.
     */
    @Override
    protected void onConnect() {
        setTimeout(false);
        super.onConnect();
    }
    
    /**
     * Időtúllépés esetén a modelben frissül a timeout paraméter és ezzel frissül a felület is.
     */
    @Override
    protected void onTimeout(Exception ex) throws Exception {
        setTimeout(true);
        super.onTimeout(ex);
    }

    /**
     * Az időtúllépés helyreállása után a modelben frissül a timeout paraméter és ezzel frissül a felület is.
     */
    @Override
    protected void afterTimeout() throws Exception {
        setTimeout(false);
        super.afterTimeout();
    }

    /**
     * Ha a kapcsolt megszakadt.
     * Az összes aktív kapcsolatfeldolgozót leállítja, mely ugyan ahhoz az eszközhöz tartozik
     * és megjeleníti a kapcsolódás hiba ablakot.
     */
    @Override
    protected void onDisconnect(Exception ex) {
        super.onDisconnect(ex);
        showConnectionStatus(Status.DISCONNECTED);
    }
    
}
