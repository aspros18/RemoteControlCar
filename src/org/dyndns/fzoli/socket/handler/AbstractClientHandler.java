package org.dyndns.fzoli.socket.handler;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.dyndns.fzoli.socket.process.ProcessException;

/**
 * Kapcsolatkezelő kliens oldalra.
 * A socket feldolgozása előtt az adatok alapján kiválasztja, melyik feldolgozót kell indítani.
 * @author zoli
 */
public abstract class AbstractClientHandler extends AbstractHandler {

    private final Integer deviceId;
    
    private Integer connectionId;
    
    /**
     * A kliens oldali kapcsolatkezelő konstruktora.
     * @param socket Socket, amin keresztül folyik a kommunikáció.
     * @param deviceId eszközazonosító, ami alapján a szerver tudja, mivel kommunikál
     * @throws IllegalArgumentException ha az eszközazonosító mérete nagyobb egy bájtnál vagy negatív
     */
    public AbstractClientHandler(Socket socket, int deviceId) {
        super(socket);
        if (deviceId < 0 || deviceId > 255) throw new IllegalArgumentException("Device ID needs to be between 1 and 255");
        this.deviceId = deviceId;
    }

    /**
     * A kapcsolatazonosító a kliens oldalon addig nem ismert, míg a szerver nem közli.
     * Ha a kapcsolat létrejön, az első bejövő bájt tartalmazza a kapcsolatazonosítót,
     * ameddig ez nem történik meg, a kapcsolatazonosító null értékű marad.
     * @return Kapcsolatazonosító, ami segítségével megtudható a kapcsolatteremtés célja.
     */
    @Override
    public Integer getConnectionId() {
        return connectionId;
    }

    private void setConnectionId(int connectionId) {
        this.connectionId = connectionId;
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
     * Az eszközazonosító küldése és a kapcsolatazonosító szervertől való fogadása után eldől,
     * melyik kapcsolatfeldolgozót kell használni és a konkrét feldolgozás kezdődik meg.
     * Ha a feldolgozás végetér, az erőforrások felszabadulnak.
     * @throws ProcessException ha bármi hiba történik
     */
    @Override
    public void run() {
        try {
            // stream referenciák megszerzése
            InputStream in = getSocket().getInputStream();
            OutputStream out = getSocket().getOutputStream();
            
            // eszközazonosító közlése a szervernek
            out.write(getDeviceId());
            
            // kapcsolatazonosító megszerzése a szervertől
            setConnectionId(in.read());
            
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
            throw new ProcessException(ex);
        }
    }
    
}
