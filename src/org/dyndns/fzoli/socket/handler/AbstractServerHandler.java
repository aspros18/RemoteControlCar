package org.dyndns.fzoli.socket.handler;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Kapcsolatkezelő szerver oldalra.
 * A socket feldolgozása előtt az adatok alapján kiválasztja, melyik feldolgozót kell indítani.
 * @author zoli
 */
public abstract class AbstractServerHandler extends AbstractHandler {

    private Integer deviceId, connectionId;
    
    /**
     * A szerver oldali kapcsolatkezelő konstruktora.
     * @param socket Socket, amin keresztül folyik a kommunikáció.
     */
    public AbstractServerHandler(Socket socket) {
        super(socket);
    }
    
    /**
     * A kapcsolatazonosító a szerver oldalon addig nem ismert, míg a kliens nem közli.
     * Ha a kapcsolat létrejön, a második bejövő bájt tartalmazza a kapcsolatazonosítót,
     * ameddig ez nem jön át, a kapcsolatazonosító null értékű marad.
     * @return Kapcsolatazonosító, ami segítségével megtudható a kapcsolatteremtés célja.
     */
    @Override
    public Integer getConnectionId() {
        return connectionId;
    }

    /**
     * Az eszközazonosító a szerver oldalon addig nem ismert, míg a kliens nem közli.
     * Ha a kapcsolat létrejön, az első bejövő bájt tartalmazza az eszközazonosítót,
     * ameddig ez nem jön át, az eszközazonosító null értékű marad.
     * @return Eszközazonosító, ami segítségével megtudható a kliens típusa.
     */
    @Override
    public Integer getDeviceId() {
        return deviceId;
    }
    
    /**
     * Beállítja az eszközazonosítót.
     * Amint a metódus lefutott, már biztonságosan elkérhető az adat.
     */
    private void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }
    
    /**
     * Beállítja a kapcsolatazonosítót.
     * Amint a metódus lefutott, már biztonságosan elkérhető az adat.
     */
    private void setConnectionId(int connectionId) {
        this.connectionId = connectionId;
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
     * Az eszköz- és kapcsolatazonosító klienstől való fogadása után eldől, melyik kapcsolatfeldolgozót
     * kell használni a szerver oldalon és a konkrét feldolgozás kezdődik meg.
     * Ha a feldolgozás végetér, az erőforrások felszabadulnak.
     * @throws HandlerException ha bármi hiba történik
     */
    @Override
    public void run() {
        try {
            // stream referenciák megszerzése
            InputStream in = getSocket().getInputStream();
            OutputStream out = getSocket().getOutputStream();
            
            // eszközazonosító elkérése a klienstől
            setDeviceId(in.read());
            
            // kapcsolatazonosító elkérése a klienstől
            setConnectionId(in.read());
            
            // inicializáló metódus futtatása
            init();
            
            // kapcsolatazonosító közlése a kliensnek
            out.write(getConnectionId());
            
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