package org.dyndns.fzoli.socket.handler;

import java.util.List;
import javax.net.ssl.SSLSocket;
import org.dyndns.fzoli.socket.handler.exception.MultipleCertificateException;
import org.dyndns.fzoli.socket.process.SecureProcess;

/**
 * Biztonságos kapcsolatkezelő szerver oldalra.
 * A socket feldolgozása előtt az adatok alapján kiválasztja, melyik feldolgozót kell indítani.
 * @author zoli
 */
public abstract class AbstractSecureServerHandler extends AbstractServerHandler implements SecureHandler {

    private String localCommonName, remoteCommonName;
    
    /**
     * A szerver oldali biztonságos kapcsolatkezelő konstruktora.
     * @param socket SSLSocket, amin keresztül folyik a kommunikáció.
     */
    public AbstractSecureServerHandler(SSLSocket socket) {
        super(socket);
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
     * Az eszköz- és kapcsolatazonosító klienstől való fogadása után eldől, melyik kapcsolatfeldolgozót
     * kell használni a szerver oldalon és a konkrét feldolgozás kezdődik meg.
     * Ha a feldolgozás végetér, az erőforrások felszabadulnak.
     * @throws HandlerException ha bármi hiba történik
     * @throws SecureHandlerException ha nem megbízható vagy hibás bármelyik tanúsítvány
     * @throws MultipleCertificateException ha ugyan azzal a tanúsítvánnyal több kliens is kapcsolódik
     */
    @Override
    public void run() {
        super.run();
    }

    /**
     * Megszerzi a helyi és távoli tanúsítvány Common Name mezőjét és ellenőrzi a tanúsítványt.
     * @throws SecureHandlerException ha nem megbízható vagy hibás bármelyik tanúsítvány
     * @throws MultipleCertificateException ha ugyan azzal a tanúsítvánnyal több kliens is kapcsolódik
     */
    @Override
    protected void init() {
        localCommonName = SecureHandlerUtil.getLocalCommonName(getSocket());
        remoteCommonName = SecureHandlerUtil.getRemoteCommonName(getSocket());
        List<SecureProcess> procs = getSecureProcesses();
        for (SecureProcess proc : procs) {
            if (proc.getHandler().isCertEqual(this)) {
                throw new MultipleCertificateException("Duplicated certificate");
            }
        }
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
     * Bezárja az összes többi kapcsolatot, ami már létre lett hozva az adott klienssel.
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
     * A titkosított kommunikáció ezen oldalán álló szerver tanúsítványának CN mezőjét adja vissza.
     */
    @Override
    public String getLocalCommonName() {
        return localCommonName;
    }

    /**
     * A titkosított kommunikáció másik oldalán álló kliens tanúsítványának CN mezőjét adja vissza.
     */
    @Override
    public String getRemoteCommonName() {
        return remoteCommonName;
    }
    
}
