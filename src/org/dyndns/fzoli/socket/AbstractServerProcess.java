package org.dyndns.fzoli.socket;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Külön szálban, a socketen át adatfeldolgozást végző szerver oldali osztály.
 * @author zoli
 */
public abstract class AbstractServerProcess extends AbstractProcess {

    private final Integer connectionId;
    
    /**
     * @param socket Socket, amin keresztül folyik a kommunikáció.
     */
    public AbstractServerProcess(Socket socket, int connectionId) {
        super(socket);
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
     * Ez a metódus fut le a szálban.
     * A kapcsolatazonosító kliensnek való elküldése után a konkrét feldolgozás kezdődik meg, és ha a feldolgozás végetér, az erőforrások felszabadulnak.
     */
    @Override
    public final void run() {
        try {
            // stream referenciák megszerzése
            InputStream in = getSocket().getInputStream();
            OutputStream out = getSocket().getOutputStream();
            
            // kapcsolatazonosító közlése a klienssel
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
