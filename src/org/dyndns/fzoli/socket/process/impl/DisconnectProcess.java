package org.dyndns.fzoli.socket.process.impl;

import org.dyndns.fzoli.socket.process.SecureProcess;

/**
 * Az implementált osztályok a másik oldallal kiépített kapcsolatot arra használják, hogy
 * másodpercenként ellenőrzik, hogy megszakadt-e a kapcsolat a másik oldallal.
 * @author zoli
 */
public interface DisconnectProcess extends SecureProcess {
    
    /**
     * A socket bementének olvasására be lehet állítani időtúllépést.
     * Erre alapozva megtudható, hogy él-e még a kapcsolat a távoli géppel.
     */
    @Override
    public void run();
    
    /**
     * Ez a metódus hívódik meg, amikor létrejön a kapcsolat.
     */
    public void onConnect();
    
    /**
     * A válaszkérés előtt hívódik meg.
     * @throws Exception az {@code onTimeout} metódusnak átadott kivétel
     */
    public void beforeAnswer() throws Exception;
    
    /**
     * Akkor hívódik meg, amikor sikeresen válasz érkezett a távoli géptől.
     * @throws Exception az {@code onTimeout} metódusnak átadott kivétel
     */
    public void afterAnswer() throws Exception;
    
    /**
     * Időtúllépés esetén hívódik meg.
     * A metódus ha kivételt dob, az {@code onDisconnect} metódus hívódik meg.
     * A metódus az elkapott kivételt dobja, így alapértelmezésként az első megszakadás
     * esetén már lefut az {@code onDisconnect}
     * @param ex a hibát okozó kivétel
     * @throws Exception az {@code onDisconnect} metódusnak átadott kivétel
     */
    public void onTimeout(Exception ex) throws Exception;
    
    /**
     * Ez a metódus hívódik meg, amikor megszakad a kapcsolat.
     * Az összes aktív kapcsolatfeldolgozót leállítja, mely ugyan ahhoz az eszközhöz tartozik.
     * @param ex a hibát okozó kivétel
     */
    public void onDisconnect(Exception ex);
    
}
