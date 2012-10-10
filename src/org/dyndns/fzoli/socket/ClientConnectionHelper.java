package org.dyndns.fzoli.socket;

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
public abstract class ClientConnectionHelper {
    
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
     * Megadja, hogy folyamatban van-e a kapcsolódás.
     */
    private boolean connecting;
    
    /**
     * Egyszerű kapcsolódást megvalósító osztály konstruktora.
     * @param deviceId eszközazonosító
     * @param connectionIds kapcsolatazonosítókat tartalmazó tömb
     */
    public ClientConnectionHelper(int deviceId, int[] connectionIds) {
        if (connectionIds == null || connectionIds.length < 1) throw new IllegalArgumentException("At least one Connection ID needs to be added");
        this.deviceId = deviceId;
        this.connectionIds = connectionIds;
    }
    
    /**
     * TODO
     * Megmondja, hogy kapcsolódva van-e a kliens a szerverhez.
     * @return true, ha az összes kapcsolat ki van alakítva a szerverrel
     */
    public boolean isConnected() {
        return CONNECTIONS.size() == connectionIds.length;
    }

    /**
     * TODO
     * Megmondja, hogy a kapcsolódás folyamatban van-e.
     * @return true, ha a kapcsolódás folyamatban van
     */
    public boolean isConnecting() {
        return connecting;
    }

    /**
     * Beállítja a kapcsolódás folyamatát.
     */
    public void setConnecting(boolean connecting) {
        System.out.println("set connecting: " + connecting);
        this.connecting = connecting;
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
     * Ha a kapcsolódás végetért, ez a metódus fut le.
     */
    protected void onConnected() {
        ;
    }
    
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
     * Ha az utolsó kapcsolat is kialakult, {@code onConnect} metódus fut le.
     * Ha bármi hiba történik, {@code onException} metódus fut le.
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
            if (connectionId == connectionIds[connectionIds.length - 1]) {
                setConnecting(false);
                onConnected();
            }
        }
        catch (Exception ex) {
            setConnecting(false);
            onException(ex, connectionId);
        }
    }
    
    /**
     * Kapcsolódás a szerverhez.
     * Ha az utolsó kapcsolat is kialakult, {@code onConnect} metódus fut le.
     * Ha bármi hiba történik a kapcsolódások közben, {@code onException} metódus fut le.
     * Ha a kapcsolódás folyamatban van már, nem csinál semmit.
     */
    public void connect() {
        if (isConnecting()) return;
        setConnecting(true);
        new Thread(new Runnable() {

            @Override
            public void run() {
                runHandler(connectionIds[0], true);
            }
            
        }).start();
    }
    
    /**
     * A kapcsolatok bezárása.
     */
    public void disconnect() {
        synchronized(CONNECTIONS) {
            setConnecting(false);
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
