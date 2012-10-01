package org.dyndns.fzoli.socket.process;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Külön szálban, a socketen át adatfeldolgozást végző kliens oldali osztály alapja.
 * @author zoli
 */
public abstract class AbstractClientProcess extends AbstractProcess {

    private final int deviceId;
    private Integer connectionId;
    
    /**
     * Kliens oldali adatfeldolgozó konstruktora.
     * @param socket Socket, amin keresztül folyik a kommunikáció.
     */
    public AbstractClientProcess(Socket socket, int deviceId) {
        super(socket);
        this.deviceId = deviceId;
    }

    /**
     * A kapcsolatazonosító a kliens oldalon addig nem ismert, míg a szerver nem közli.
     * Ha a kapcsolat létrejön, az első bejövő bájt tartalmazza a kapcsolatazonosítót,
     * ameddig ez nem történik meg, a kapcsolatazonosító null értékű marad.
     * @return Kapcsolatazonosító, ami segítségével megtudható a kapcsolatteremtés célja.
     */
    @Override
    public final Integer getConnectionId() {
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
    public final Integer getDeviceId() {
        return deviceId;
    }
    
    /**
     * Ez a metódus fut le a szálban.
     * A kapcsolatazonosító szervertől való fogadása után a konkrét feldolgozás kezdődik meg, és ha a feldolgozás végetér, az erőforrások felszabadulnak.
     * @throws ProcessException ha bármi hiba történik
     */
    @Override
    public final void run() {
        try {
            // stream referenciák megszerzése
            InputStream in = getSocket().getInputStream();
            OutputStream out = getSocket().getOutputStream();
            
            // eszközazonosító közlése a szervernek
            out.write(getDeviceId());
            
            // kapcsolatazonosító megszerzése a szervertől
            setConnectionId(in.read());
            
            // adatfeldolgozás
            process();
            
            // kapcsolat bezárása
            in.close();
            out.flush();
            out.close();
        }
        catch (Exception ex) {
            throw new ProcessException(ex);
        }
    }
    
    /**
     * Kliens oldali adatfeldolgozó metódus.
     */
    protected abstract void process();

}
