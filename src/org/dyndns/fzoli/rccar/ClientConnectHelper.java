package org.dyndns.fzoli.rccar;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.net.ssl.SSLSocket;
import org.dyndns.fzoli.socket.handler.AbstractSecureClientHandler;
import org.dyndns.fzoli.socket.handler.Handler;
import org.dyndns.fzoli.socket.handler.event.HandlerListener;

/**
 * Kliens oldalra egyszerű kapcsolódást megvalósító osztály.
 * @author zoli
 */
public abstract class ClientConnectHelper {

    /**
     * Eszközazonosító.
     */
    private final int deviceId;
    
    /**
     * Kapcsolatazonosítók.
     */
    private final int[] connectionIds;

    /**
     * A kiépített kapcsolatokat tartalmazó lista.
     */
    private final List<SSLSocket> CONNECTIONS = new ArrayList<SSLSocket>();
    
    /**
     * Eseménykezelő, ami lefut, ha sikerült az első kapcsolódás.
     * Ha az első kapcsolódás sikerült, létrehozza a többi kapcsolatot is.
     */
    private final HandlerListener listener = new HandlerListener() {

        @Override
        public void onProcessSelected(Handler handler) {
            synchronized(connectionIds) {
                for (int i = 1; i < connectionIds.length; i++) {
                    runHandler(connectionIds[i], false);
                }
            }
        }
        
    };
    
    /**
     * Egyszerű kapcsolódást megvalósító osztály konstruktora.
     * @param deviceId eszközazonosító
     * @param connectionIds kapcsolatazonosítókat tartalmazó tömb
     */
    public ClientConnectHelper(int deviceId, int[] connectionIds) {
        if (connectionIds == null || connectionIds.length < 1) throw new IllegalArgumentException("At least one Connection ID needs to be added");
        this.deviceId = deviceId;
        this.connectionIds = connectionIds;
    }
    
    /**
     * Socket létrehozása.
     * Kapcsolódás a szerverhez.
     */
    protected abstract SSLSocket createConnection() throws GeneralSecurityException, IOException;
    
    /**
     * Kliens oldali Handler példányosítása.
     * @param socket a kapcsolat a szerverrel
     * @param deviceId az eszközazonosító
     * @param connectionId a kapcsolatazonosító
     */
    protected abstract AbstractSecureClientHandler createHandler(SSLSocket socket, int deviceId, int connectionId);
    
    /**
     * Ha kivétel keletkezik, ebben a metódusban le lehet kezelni.
     * @param ex a keletkezett kivétel
     * @param connectionId a közben használt kapcsolatazonosító
     */
    protected void onException(Exception ex, int connectionId) {
        throw new RuntimeException(ex.getMessage() + "; connection id: " + connectionId, ex);
    }
    
    /**
     * Kapcsolódik a szerverhez a megadott kapcsolatazonosítóval.
     * A létrehozott socketet eltárolja a listában.
     * @param connectionId a kapcsolatazonosító
     * @param addListener megadja, kell-e eseményt hozzáadni
     */
    private void runHandler(int connectionId, boolean addListener) {
        try {
            SSLSocket conn = createConnection();
            synchronized(CONNECTIONS) {
                CONNECTIONS.add(conn);
            }
            AbstractSecureClientHandler handler = createHandler(conn, deviceId, connectionId);
            if (addListener) handler.addHandlerListener(listener);
            new Thread(handler).start();
        }
        catch (Exception ex) {
            onException(ex, connectionId);
        }
    }
    
    /**
     * Kapcsolódás a szerverhez.
     */
    public void connect() {
        runHandler(connectionIds[0], true);
    }
    
    /**
     * A kapcsolatok bezárása.
     */
    public void disconnect() {
        synchronized(CONNECTIONS) {
            Iterator<SSLSocket> it = CONNECTIONS.iterator();
            while (it.hasNext()) {
                SSLSocket conn = it.next();
                it.remove();
                try {
                    conn.close();
                }
                catch (Exception ex) {
                    ;
                }
            }
        }
    }
    
}
