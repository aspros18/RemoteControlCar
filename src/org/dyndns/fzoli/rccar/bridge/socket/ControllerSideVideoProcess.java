package org.dyndns.fzoli.rccar.bridge.socket;

import java.io.IOException;
import java.net.Socket;
import org.dyndns.fzoli.rccar.model.bridge.ControllerStorage;
import org.dyndns.fzoli.rccar.model.bridge.HostStorage;
import org.dyndns.fzoli.rccar.model.bridge.StorageList;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.impl.SharedJpegProvider;
import org.dyndns.fzoli.socket.process.AbstractSecureProcess;

/**
 * A hoszt által streamelt JPEG képkockákat küldi tovább a vezérlő programnak.
 * @author zoli
 */
public class ControllerSideVideoProcess extends AbstractSecureProcess {

    /**
     * Az MJPEG képkockákat szolgáltató osztály.
     * Mindig az aktuálisan kiválasztott jármű képkockáját adja vissza.
     */
    private static class VehicleJpegProvider extends SharedJpegProvider {

        /**
         * A vezérlő kliensprogram tanúsítványneve.
         */
        private final String name;
        
        /**
         * A Process-socket referenciája.
         */
        private final Socket socket;
        
        /**
         * A kliensprogramhoz tartozó tároló referenciája.
         * Kezdetben nincs megadva, de a {@link #getKey()} metódus beállítja, ha tudja.
         */
        private ControllerStorage cs;
        
        /**
         * Konstruktor.
         * @param name a kliens tanúsítványneve
         * @param out a stream, melyre megy az MJPEG folyam
         */
        public VehicleJpegProvider(String name, Socket socket) throws IOException {
            super(null, socket.getOutputStream());
            this.socket = socket;
            this.name = name;
        }

        /**
         * Megadja a kiválasztott jármű nevét, ami a kulcs.
         * TODO: Egyelőre teszt módban van
         * @return a kulcs, vagy null, ha nincs jármű kiválasztva
         */
        @Override
        public String getKey() {
            if (cs == null) cs = StorageList.findControllerStorageByName(name);
            if (cs == null) return "teszt1"; //return null;
            HostStorage s = cs.getHostStorage();
            if (s == null) return "teszt1"; //return null;
            System.out.println("controller side video key: " + s.getName());
            return "teszt1"; //return s.getName();
        }

        @Override
        protected boolean isInterrupted() {
            return socket.isClosed();
        }

        @Override
        protected boolean onException(Exception ex) {
            ex.printStackTrace();
            return !socket.isClosed();
        }
        
    }
    
    /**
     * Biztonságos MJPEG stream küldő inicializálása.
     * @param handler Biztonságos kapcsolatfeldolgozó, ami létrehozza ezt az adatfeldolgozót.
     * @throws NullPointerException ha handler null
     */
    public ControllerSideVideoProcess(SecureHandler handler) {
        super(handler);
    }

    /**
     * Mindig az aktuális jármű képkockájáit streameli a vezérlő program felé.
     * Ha valami hiba történik közben, az összes kapcsolatot megszakítja a vezérlő programmal.
     */
    @Override
    public void run() {
        try {
            new VehicleJpegProvider(getRemoteCommonName(), getSocket()).handleConnection();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            getHandler().closeProcesses();
        }
    }
    
}
