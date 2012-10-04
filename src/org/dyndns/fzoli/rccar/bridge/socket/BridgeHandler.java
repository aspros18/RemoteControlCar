package org.dyndns.fzoli.rccar.bridge.socket;

import java.awt.TrayIcon;
import java.net.SocketException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSocket;
import static org.dyndns.fzoli.rccar.SystemTrayIcon.showMessage;
import static org.dyndns.fzoli.rccar.bridge.Main.VAL_WARNING;
import org.dyndns.fzoli.rccar.test.DummyProcess;
import org.dyndns.fzoli.socket.handler.AbstractSecureServerHandler;
import org.dyndns.fzoli.socket.handler.MultipleCertificateException;
import org.dyndns.fzoli.socket.handler.RemoteHandlerException;
import org.dyndns.fzoli.socket.process.AbstractSecureProcess;

/**
 * A híd kapcsolatkezelője.
 * @author zoli
 */
public class BridgeHandler extends AbstractSecureServerHandler {

    /**
     * Alapértelmezetten a figyelmeztetések be vannak kapcsolva.
     */
    private static boolean show = true;
    
    /**
     * A híd biztonságos kapcsolatkezelő konstruktora.
     * @param socket SSLSocket, amin keresztül folyik a kommunikáció.
     */
    public BridgeHandler(SSLSocket socket) {
        super(socket);
    }

    public static boolean isWarnEnabled() {
        return show;
    }
    
    /**
     * Bekapcsolja vagy kikapcsolja a figyelmeztetéseket.
     */
    public static void setWarnEnabled(boolean enabled) {
        show = enabled;
    }
    
    /**
     * Ha adott klienstől az első kapcsolatfelvétel közben hiba keletkezik, jelzi a felhasználónak.
     * @param message a kijelzendő üzenet
     */
    private void showWarning(String message) {
        if (getConnectionId() == null || getConnectionId().equals(0))
            if (getSocket() != null && isWarnEnabled()) showMessage(VAL_WARNING, message + " a " + getSocket().getInetAddress().getHostName() + " címről.", TrayIcon.MessageType.WARNING);
    }
    
    /**
     * Ha adott klienstől az első kapcsolatfelvétel közben hiba keletkezik, jelzi a felhasználónak.
     * @param message a kijelzendő üzenet
     * @param details további részlet az üzenet mellé
     */
    private void showWarning(String message, String details) {
        if (details == null || details.isEmpty()) showWarning(message);
        else showWarning(message + " (" + details + ")");
    }
    
    /**
     * Ha kivétel képződik a szálban, fel kell dolgozni.
     * Duplázott tanúsítvány esetén figyelmezteti a felhasználót.
     * Más kivétel nem várt hibát eredményeznek.
     * @param ex a kivétel
     * @throws Exception a paraméterben átadott kivételt, ha nem várt hiba történt
     */
    @Override
    protected void onException(Exception ex) {
        try {
            throw ex;
        }
        catch (MultipleCertificateException e) {
            showWarning("Duplázott tanúsítvány");
        }
        catch (SSLHandshakeException e) {
            showWarning("Nem megbízható kapcsolódás");
        }
        catch (RemoteHandlerException e) {
            showWarning("Távoli hiba", e.getMessage());
        }
        catch (SocketException e) {
            showWarning("Socket hiba", e.getMessage());
        }
        catch (Exception e) {
            // nem várt hiba jelzése
            super.onException(e);
        }
    }
    
    /**
     * Kiválasztja a biztonságos kapcsolatfeldolgozó objektumot az adatok alapján és elindítja.
     * TODO: egyelőre teszt
     */
    @Override
    protected AbstractSecureProcess selectProcess() {
        switch (getConnectionId()) {
            case 0:
                return new BridgeDisconnectProcess(this);
            default:
                return new DummyProcess(this);
        }
    }
    
}
