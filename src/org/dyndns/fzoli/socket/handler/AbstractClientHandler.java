package org.dyndns.fzoli.socket.handler;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Kapcsolatkezelő kliens oldalra.
 * A socket feldolgozása előtt az adatok alapján kiválasztja, melyik feldolgozót kell indítani.
 * @author zoli
 */
public abstract class AbstractClientHandler extends AbstractHandler {

    private final Integer deviceId, connectionId;
    
    /**
     * A kliens oldali kapcsolatkezelő konstruktora.
     * @param socket Socket, amin keresztül folyik a kommunikáció.
     * @param deviceId eszközazonosító, ami alapján a szerver tudja, mivel kommunikál
     * @param connectionId kapcsolatazonosító, ami alapján a szerver tudja, mi a kérés
     * @throws IllegalArgumentException ha az eszközazonosító vagy a kapcsolatazonosító mérete nagyobb egy bájtnál vagy negatív értékű
     */
    public AbstractClientHandler(Socket socket, int deviceId, int connectionId) {
        super(socket);
        checkId("Device", deviceId);
        checkId("Connection", connectionId);
        this.deviceId = deviceId;
        this.connectionId = connectionId;
    }

    /**
     * Ellenőrzi az azonosítót, és ha mérete nagyobb egy bájtnál vagy negatív értékű, kivételt dob.
     * @throws IllegalArgumentException ha az azonosító értéke nem megengedett
     */
    private void checkId(String name, int id) {
        if (id < 0 || id > 255) throw new IllegalArgumentException(name + " ID needs to be between 1 and 255");
    }
    
    /**
     * A kapcsolatazonosítót a kliens generálja, ezért soha nem null.
     * @return Kapcsolatazonosító, ami segítségével megtudható a kapcsolatteremtés célja.
     */
    @Override
    public Integer getConnectionId() {
        return connectionId;
    }

    /**
     * Az eszközazonosítót a kliens generálja, ezért soha nem null.
     * @return Eszközazonosító, ami segítségével megtudható a kliens típusa.
     */
    @Override
    public Integer getDeviceId() {
        return deviceId;
    }

    /**
     * Miután az eszközazonosító és a kapcsolatazonosító közlése megtörtént,
     * lefut ez az inicializáló metódus, ami után a konkrét feldolgozás történik meg.
     * Ez a metódus az utód osztályoknak lett létrehozva inicializálás céljára.
     */
    protected void init() {
    }
    
    /**
     * Ez a metódus fut le a szálban.
     * Az eszköz- és kapcsolatazonosító szervernek való elküldése után eldől, melyik kapcsolatfeldolgozót
     * kell használni a kliens oldalon és a konkrét feldolgozás kezdődik meg.
     * Ha a feldolgozás végetér, az erőforrások felszabadulnak.
     * @throws HandlerException ha bármi hiba történik
     */
    @Override
    public void run() {
        try {
            // stream referenciák megszerzése
            InputStream in = getSocket().getInputStream();
            OutputStream out = getSocket().getOutputStream();
            
            // eszközazonosító küldése a szervernek
            out.write(getDeviceId());
            // kapcsolatazonosító küldése a szervernek
            out.write(getConnectionId());
            
            // inicializáló metódus futtatása
            init();
            
            // adatfeldolgozó kiválasztása és futtatása
            selectProcess().run();
            
            // kapcsolat bezárása
            in.close();
            out.flush();
            out.close();
        }
        catch (Exception ex) {
            throw new HandlerException(ex);
        }
    }
    
}
