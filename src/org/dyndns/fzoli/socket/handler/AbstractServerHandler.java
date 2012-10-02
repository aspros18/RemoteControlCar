package org.dyndns.fzoli.socket.handler;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.dyndns.fzoli.socket.process.ProcessException;

/**
 * Kapcsolatkezelő szerver oldalra.
 * A socket feldolgozása előtt az adatok alapján kiválasztja, melyik feldolgozót kell indítani.
 * @author zoli
 */
public abstract class AbstractServerHandler extends AbstractHandler {

    private final Integer connectionId;
    
    private Integer deviceId;
    
    /**
     * A szerver oldali kapcsolatkezelő konstruktora.
     * @param socket Socket, amin keresztül folyik a kommunikáció.
     * @param connectionId kapcsolatazonosító, ami alapján a kliens tudja, mi a dolga
     * @throws IllegalArgumentException ha a kapcsolatazonosító mérete nagyobb egy bájtnál vagy negatív
     */
    public AbstractServerHandler(Socket socket, int connectionId) {
        super(socket);
        if (connectionId < 0 || connectionId > 255) throw new IllegalArgumentException("Connection ID needs to be between 1 and 255");
        this.connectionId = connectionId;
    }

    /**
     * @return Kapcsolatazonosító, ami segítségével megtudható a kapcsolatteremtés célja.
     */
    @Override
    public Integer getConnectionId() {
        return connectionId;
    }

    /**
     * @return Eszközazonosító, ami segítségével megtudható a kliens típusa.
     */
    @Override
    public Integer getDeviceId() {
        return deviceId;
    }
    
    private void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }
    
    /**
     * Ez a metódus fut le a szálban.
     * Az eszközazonosító fogadása és a kapcsolatazonosító kliensnek való elküldése után eldől,
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
            
            // eszközazonosító elkérése a klienstől
            setDeviceId(in.read());
            
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
            throw new ProcessException(ex);
        }
    }
    
}
