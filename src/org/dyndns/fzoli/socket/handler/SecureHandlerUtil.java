package org.dyndns.fzoli.socket.handler;

import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocket;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;
import org.dyndns.fzoli.socket.process.Process;
import org.dyndns.fzoli.socket.process.SecureProcess;

/**
 * Segédosztály a SecureHandler interfész implementálásához.
 * @author zoli
 */
class SecureHandlerUtil {
    
    /**
     * Ha kivétel képződik, fel kell dolgozni.
     * A SecureHandlert implementáló osztályban elsőként ezt a metódust, majd az ős metódusát kell meghívni.
     * @param ex a kivétel
     * @throws SecureHandlerException ha nem sikerül az SSL kézfogás
     */
    public static void onException(Exception ex) {
        if (ex instanceof SSLHandshakeException) throw new SecureHandlerException(ex);
    }
    
    /**
     * Igaz, ha ugyan azzal a tanúsítvánnyal és azonosítókkal rendelkezik a két feldolgozó.
     * @param h1 az egyik feldolgozó
     * @param h2 a másik feldolgozó
     */
    public static boolean isCertEqual(SecureHandler h1, SecureHandler h2) {
        return isCertEqual(h1, h2.getRemoteCommonName(), h2.getDeviceId(), h2.getConnectionId());
    }
    
    /**
     * Igaz, ha ugyan azzal a tanúsítvánnyal és azonosítókkal rendelkezik a feldolgozó, mint a paraméterben megadottak.
     * @param h a feldolgozó
     * @param remoteName tanúsítvány common name
     * @param deviceId eszközazonosító
     * @param connectionId kapcsolatazonosító
     */
    public static boolean isCertEqual(SecureHandler h, String remoteName, int deviceId, int connectionId) {
        return h.getRemoteCommonName().equals(remoteName) && h.getDeviceId().equals(deviceId) && h.getConnectionId().equals(connectionId);
    }
    
    /**
     * A paraméterben átadott listát leszűri.
     */
    public static List<SecureProcess> getSecureProcesses(List<Process> processes) {
        List<SecureProcess> ls = new ArrayList<SecureProcess>();
        for (Process proc : processes) {
            if (proc instanceof SecureProcess)
                ls.add((SecureProcess) proc);
        }
        return ls;
    }
    
    /**
     * A titkosított kommunikáció ezen oldalán álló gép tanúsítványának CN mezőjét adja vissza.
     * @throws SecureHandlerException ha a tanúsítvány hibás
     */
    public static String getLocalCommonName(SSLSocket socket) {
        try {
            checkSession(socket);
            return getCommonName(socket.getSession().getLocalCertificates()[0]);
        }
        catch (CertificateException ex) {
            throw new SecureHandlerException(ex);
        }
        catch (CertificateEncodingException ex) {
            throw new SecureHandlerException(ex);
        }
    }
    
    /**
     * A titkosított kommunikáció másik oldalán álló gép tanúsítványának CN mezőjét adja vissza.
     * @throws SecureHandlerException ha nem megbízható a kapcsolat vagy a tanúsítvány hibás
     */
    public static String getRemoteCommonName(SSLSocket socket) {
        try {
            checkSession(socket);
            return getCommonName(socket.getSession().getPeerCertificates()[0]);
        }
        catch (SSLPeerUnverifiedException ex) {
            throw new SecureHandlerException(ex);
        }
        catch (CertificateException ex) {
            throw new SecureHandlerException(ex);
        }
        catch (CertificateEncodingException ex) {
            throw new SecureHandlerException(ex);
        }
    }
    
    /**
     * Az SSLSocket munkamenetének ellenőrzése.
     * @throws SecureHandlerException ha a munkamenet nem érvényes
     */
    private static void checkSession(SSLSocket socket) {
        if (socket == null || socket.getSession() == null || socket.getSession().getLocalCertificates() == null || (!socket.getSession().isValid())) throw new SecureHandlerException("Invalid certificate. Please, check your CA.");
    }
    
    /**
     * A tanúsítvány CN mezőjét adja vissza.
     * @throws CertificateException ha a tanúsítvány nem X509 szabvány szerint kódolt
     * @throws CertificateEncodingException ha a tanúsítvány nem X509 szabvány szerint kódolt
     */
    private static String getCommonName(Certificate cert) throws CertificateException, CertificateEncodingException {
        String certdata = getPrincipal(cert);
        int cnstart = certdata.indexOf("CN=") + 3; // "CN=" résztől ...
        int cnstop = certdata.indexOf(',', cnstart); // ... a vesszőig ...
        if (cnstop == -1) cnstop = certdata.length(); // ... vagy ha nincs vessző, a végéig ...
        return certdata.substring(cnstart, cnstop); // ... kérem a string tartalmát, ami a tanúsítványban szereplő Common Name (CN)
    }
    
    /**
     * A tanúsítvány alapadatait adja vissza.
     * @throws CertificateException ha a tanúsítvány nem X509 szabvány szerint kódolt
     * @throws CertificateEncodingException ha a tanúsítvány nem X509 szabvány szerint kódolt
     */
    private static String getPrincipal(Certificate cert) throws CertificateException, CertificateEncodingException {
        return X509Certificate.getInstance(cert.getEncoded()).getSubjectDN().getName();
    }
    
}
