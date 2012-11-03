package org.dyndns.fzoli.socket.handler;

import java.util.List;
import javax.net.ssl.SSLSocket;
import org.dyndns.fzoli.socket.process.SecureProcess;

/**
 * Biztonságos kapcsolatkezelő kliens oldalra.
 * A socket feldolgozása előtt az adatok alapján kiválasztja, melyik feldolgozót kell indítani.
 * @author zoli
 */
public abstract class AbstractSecureClientHandler extends AbstractClientHandler implements SecureHandler {

    private String localCommonName, remoteCommonName;
    
    /**
     * A kliens oldali biztonságos kapcsolatkezelő konstruktora.
     * @param socket SSLSocket, amin keresztül folyik a kommunikáció.
     * @param deviceId eszközazonosító, ami alapján a szerver tudja, mivel kommunikál
     * @param connectionId kapcsolatazonosító, ami alapján a szerver tudja, mi a kérés
     * @throws IllegalArgumentException ha az eszközazonosító vagy a kapcsolatazonosító mérete nagyobb egy bájtnál vagy negatív értékű
     */
    public AbstractSecureClientHandler(SSLSocket socket, int deviceId, int connectionId) {
        super(socket, deviceId, connectionId);
    }

    /**
     * Azokat a biztonságos adatfeldolgozókat adja vissza, melyek még dolgoznak.
     */
    @Override
    public List<SecureProcess> getSecureProcesses() {
        return SecureHandlerUtil.getSecureProcesses(getProcesses());
    }

    /**
     * Igaz, ha ugyan azzal a tanúsítvánnyal és azonosítókkal rendelkezik a megadott feldolgozó.
     * @param handler a másik feldolgozó
     */
    @Override
    public boolean isCertEqual(SecureHandler handler) {
        return SecureHandlerUtil.isCertEqual(this, handler);
    }

    /**
     * Igaz, ha ugyan azzal a tanúsítvánnyal és azonosítókkal rendelkezik a feldolgozó, mint a paraméterben megadottak.
     * @param remoteName tanúsítvány common name
     * @param deviceId eszközazonosító
     * @param connectionId kapcsolatazonosító
     */
    @Override
    public boolean isCertEqual(String remoteName, int deviceId, int connectionId) {
        return SecureHandlerUtil.isCertEqual(this, remoteName, deviceId, connectionId);
    }
    
    /**
     * Ez a metódus fut le a szálban.
     * Az eszköz- és kapcsolatazonosító szervernek való elküldése után eldől, melyik kapcsolatfeldolgozót
     * kell használni a kliens oldalon és a konkrét feldolgozás kezdődik meg.
     * Ha a feldolgozás végetér, az erőforrások felszabadulnak.
     * @throws HandlerException ha bármi hiba történik
     * @throws SecureHandlerException ha nem megbízható vagy hibás bármelyik tanúsítvány
     */
    @Override
    public void run() {
        super.run();
    }
    
    /**
     * Megszerzi a helyi és távoli tanúsítvány Common Name mezőjét.
     * @throws SecureHandlerException ha nem megbízható vagy hibás bármelyik tanúsítvány
     */
    @Override
    protected void init() {
        localCommonName = SecureHandlerUtil.getLocalCommonName(getSocket());
        remoteCommonName = SecureHandlerUtil.getRemoteCommonName(getSocket());
    }

    /**
     * Ha kivétel képződik, fel kell dolgozni.
     * @param ex a kivétel
     * @throws HandlerException ha nem RuntimeException a kivétel
     * @throws SecureHandlerException ha nem sikerül az SSL kézfogás
     * @throws RuntimeException ha RuntimeException a kivétel
     */
    @Override
    protected void onException(Exception ex) {
        SecureHandlerUtil.onException(ex);
        super.onException(ex);
    }

    /**
     * Ha a kiválasztott Process null, fel kell dolgozni.
     * Bezárja az összes többi kapcsolatot, ami már létre lett hozva a szerverrel.
     */
    @Override
    protected void onProcessNull() {
        super.onProcessNull();
        closeProcesses();
    }
    
    /**
     * Kiválasztja a biztonságos kapcsolatfeldolgozó objektumot az adatok alapján és elindítja.
     * A metódus csak akkor hívható meg, amikor már ismert a kapcsolatazonosító és eszközazonosító.
     */
    @Override
    protected abstract SecureProcess selectProcess();

    /**
     * Bezárja a kapcsolatkezelőhöz tartozó kapcsolatfeldolgozók kapcsolatait.
     */
    @Override
    public void closeProcesses() {
        SecureHandlerUtil.closeProcesses(this);
    }
    
    /**
     * @return SSLSocket, amin keresztül folyik a titkosított kommunikáció.
     */
    @Override
    public SSLSocket getSocket() {
        return (SSLSocket) super.getSocket();
    }

    /**
     * A titkosított kommunikáció ezen oldalán álló kliens tanúsítványának CN mezőjét adja vissza.
     */
    @Override
    public String getLocalCommonName() {
        return localCommonName;
    }

    /**
     * A titkosított kommunikáció másik oldalán álló szerver tanúsítványának CN mezőjét adja vissza.
     */
    @Override
    public String getRemoteCommonName() {
        return remoteCommonName;
    }
    
}
