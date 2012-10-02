package org.dyndns.fzoli.socket.process;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Külön szálban, a socketen át adatfeldolgozást végző szerver oldali osztály alapja.
 * @author zoli
 */
public abstract class AbstractServerProcess extends AbstractProcess {

    private final Integer connectionId;
    
    private Integer deviceId;
    
    /**
     * Szerver oldali adatfeldolgozó konstruktora.
     * @param socket Socket, amin keresztül folyik a kommunikáció.
     */
    public AbstractServerProcess(Socket socket, int connectionId) {
        super(socket);
        if (connectionId < 0 || connectionId > 255) throw new IllegalArgumentException("Connection ID needs to be between 1 and 255");
        this.connectionId = connectionId;
    }

    /**
     * A kapcsolatazonosító a szerver oldalon mindig ismert, mivel a szerver generálja, ezért soha nem lesz null.
     * @return Kapcsolatazonosító, ami segítségével megtudható a kapcsolatteremtés célja.
     */
    @Override
    public final Integer getConnectionId() {
        return connectionId;
    }

    /**
     * Az eszközazonosítót a kliens generálja, ezért a kapcsolat létrejöttéig értéke null.
     * @return Eszközazonosító, ami segítségével megtudható a kliens típusa.
     */
    @Override
    public final Integer getDeviceId() {
        return deviceId;
    }
    
    private void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }
    
    /**
     * Ez a metódus fut le a szálban.
     * Az eszközazonosító fogadása és a kapcsolatazonosító kliensnek való elküldése után a konkrét feldolgozás kezdődik meg, és ha a feldolgozás végetér, az erőforrások felszabadulnak.
     * @throws ProcessException ha bármi hiba történik
     */
    @Override
    public final void run() {
        try {
            // stream referenciák megszerzése
            InputStream in = getSocket().getInputStream();
            OutputStream out = getSocket().getOutputStream();
            
            // eszközazonosító elkérése a klienstől
            setDeviceId(in.read());
            
            // kapcsolatazonosító közlése a kliensnek
            out.write(getConnectionId());
            
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
     * Szerver oldali adatfeldolgozó metódus.
     */
    protected abstract void process();

}
