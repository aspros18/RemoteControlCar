package org.dyndns.fzoli.rccar.bridge.socket;

import java.io.EOFException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSocket;
import org.dyndns.fzoli.rccar.ConnectionKeys;
import static org.dyndns.fzoli.rccar.bridge.ConnectionAlert.logMessage;
import org.dyndns.fzoli.rccar.bridge.Main;
import static org.dyndns.fzoli.rccar.bridge.Main.VAL_WARNING;
import org.dyndns.fzoli.rccar.bridge.config.Permissions;
import org.dyndns.fzoli.socket.handler.AbstractSecureServerHandler;
import org.dyndns.fzoli.socket.handler.exception.MultipleCertificateException;
import org.dyndns.fzoli.socket.handler.exception.RemoteHandlerException;
import org.dyndns.fzoli.socket.process.AbstractSecureProcess;
import org.dyndns.fzoli.ui.systemtray.TrayIcon.IconType;

/**
 * A híd kapcsolatkezelője.
 * @author zoli
 */
public class BridgeHandler extends AbstractSecureServerHandler implements ConnectionKeys {

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

    /**
     * Miután inicializálódtak az adatok, a híd szerver megnézi, hogy a kapcsolódott kliens tanúsítványneve blokkolva van-e,
     * és ha blokkolva van, kivételt dob, tehát a kapcsolatot elutasítja.
     * Ha egy vezérlő kapcsolódott a hídhoz és szigorú (strict) módban fut a program, a kapcsolat akkor is elutasításra kerül,
     * ha nem szerepel a fehérlistában a vezérlő tanúsítványának neve.
     */
    @Override
    protected void init() {
        super.init();
        if (Permissions.getConfig().isBlocked(getRemoteCommonName())) throw new BlockedCommonNameException();
        if (Main.CONFIG.isStrict() && getDeviceId().equals(KEY_DEV_CONTROLLER) && !Permissions.getConfig().isControllerWhite(getRemoteCommonName())) throw new BlockedCommonNameException();
    }

    /**
     * Megadja, hogy a figyelmeztetések megjelenhetnek-e.
     */
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
     * Ha adott klienstől az első kapcsolatfelvétel közben hiba keletkezik, jelzi a felhasználónak és naplózza a hibát.
     * @param message a kijelzendő üzenet
     */
    private void showWarning(String message) {
        if (getConnectionId() == null || getConnectionId().equals(0))
            if (getSocket() != null) logMessage(VAL_WARNING, message + " a " + getSocket().getInetAddress().getHostName() + " címről.", IconType.WARNING, isWarnEnabled());
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
        catch (BlockedCommonNameException e) {
            showWarning("Tiltott kapcsolódás");
        }
        catch (MultipleCertificateException e) {
            showWarning("Duplázott tanúsítvány");
        }
        catch (SSLHandshakeException e) {
            showWarning(e.getMessage().contains("Extended key usage") ? "Szerverhez való tanúsítvány használat" : "Nem megbízható kapcsolódás");
        }
        catch (SSLException e) {
            showWarning("SSL hiba", e.getMessage());
        }
        catch (RemoteHandlerException e) {
            showWarning("Távoli hiba", e.getMessage());
        }
        catch (SocketException e) {
            showWarning("Socket hiba", e.getMessage());
        }
        catch (SocketTimeoutException e) {
            showWarning("Socket időtúllépés", e.getMessage());
        }
        catch (EOFException e) {
            showWarning("Socket váratlan vég", e.getMessage());
        }
        catch (Exception e) {
            // nem várt hiba jelzése
            super.onException(e);
        }
    }
    
    /**
     * Kiválasztja a biztonságos kapcsolatfeldolgozó objektumot az adatok alapján és elindítja.
     */
    @Override
    protected AbstractSecureProcess selectProcess() {
        final boolean controller = getDeviceId().equals(KEY_DEV_CONTROLLER);
        switch (getConnectionId()) {
            case KEY_CONN_DISCONNECT:
                if (controller) return new ControllerSideDisconnectProcess(this);
                else return new HostSideDisconnectProcess(this);
            case KEY_CONN_MESSAGE:
                if (controller) return new ControllerSideMessageProcess(this);
                else return new HostSideMessageProcess(this);
            case KEY_CONN_VIDEO_STREAM:
                if (controller) return new ControllerSideVideoProcess(this);
                else return new HostSideVideoProcess(this);
        }
        return null;
    }
    
}
