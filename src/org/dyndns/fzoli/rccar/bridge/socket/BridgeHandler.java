package org.dyndns.fzoli.rccar.bridge.socket;

import java.io.EOFException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSocket;
import org.dyndns.fzoli.rccar.ConnectionKeys;
import static org.dyndns.fzoli.rccar.bridge.ConnectionAlert.logMessage;
import org.dyndns.fzoli.rccar.bridge.Main;
import static org.dyndns.fzoli.rccar.bridge.Main.VAL_WARNING;
import static org.dyndns.fzoli.rccar.bridge.Main.getString;
import org.dyndns.fzoli.rccar.bridge.config.Permissions;
import org.dyndns.fzoli.socket.handler.AbstractSecureServerHandler;
import org.dyndns.fzoli.socket.handler.exception.MultipleCertificateException;
import org.dyndns.fzoli.socket.handler.exception.RemoteHandlerException;
import org.dyndns.fzoli.socket.handler.exception.SecureHandlerException;
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
     * Létrehoz egy listát, melyben azok a kapcsolatazonosítók szerepelnek,
     * melyek kapcsolatainak kezelésekor keletkező hibák figyelmen kívül lesznek hagyva.
     */
    private static List<Integer> createHiddenWarningKeys() {
        ArrayList<Integer> l = new ArrayList<Integer>();
        l.add(KEY_CONN_MESSAGE);
        l.add(KEY_CONN_VIDEO_STREAM);
        return l;
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
     * Kapcsolatazonosítók, melyek kapcsolataiban keletkező hibák nem érdekesek.
     */
    private static final List<Integer> HIDDEN_WARN_KEYS = createHiddenWarningKeys();
    
    /**
     * A magyar nyelv magánhangzóit tartalmazza.
     * Arra kell, hogy el lehessen dönteni, a vagy az névelő kerüljön-e a cím elé.
     */
    private static final char[] mgh = {'a', 'á', 'e', 'é', 'i', 'í', 'o', 'ó', 'ö', 'ő', 'u', 'ú', 'ü', 'ű'};
    
    /**
     * Ha adott klienstől az első kapcsolatfelvétel közben hiba keletkezik, jelzi a felhasználónak és naplózza a hibát.
     * @param message a kijelzendő üzenet
     */
    private void showWarning(String message) {
        if ((getConnectionId() == null || !HIDDEN_WARN_KEYS.contains(getConnectionId())) && getSocket() != null) {
            String name = getSocket().getInetAddress().getHostName();
            String close = getString("warn_addr_prep2");
            logMessage(VAL_WARNING, message + ' ' + getString("warn_addr_prep1" + (Arrays.binarySearch(mgh, name.charAt(0)) >= 0 ? 'b' : 'a')) + ' ' + name + " [" + getSocket().getInetAddress().getHostAddress() + "]" + (close.trim().isEmpty() ? "" : " ") + close + '.', IconType.WARNING, isWarnEnabled());
        }
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
            showWarning(getString("warn_conn1")); // tiltott kapcsolódás
        }
        catch (MultipleCertificateException e) {
            showWarning(getString("warn_conn2")); // duplázott tanúsítvány
        }
        catch (SecureHandlerException e) {
            showWarning(getString("warn_conn5"), e.getMessage()); // tanúsítvány hiba
        }
        catch (SSLHandshakeException e) {
            // szerverhez való tanúsítvány érkezett vagy nem megbízható kapcsolódás
            showWarning(getString("warn_conn" + (e.getMessage().contains("Extended key usage") ? '3' : '4')));
        }
        catch (SSLException e) {
            showWarning(getString("warn_conn6"), e.getMessage()); // SSL hiba
        }
        catch (RemoteHandlerException e) {
            showWarning(getString("warn_conn7"), e.getMessage()); // távoli hiba
        }
        catch (SocketException e) {
            showWarning(getString("warn_conn8"), e.getMessage()); // socket hiba
        }
        catch (SocketTimeoutException e) {
            showWarning(getString("warn_conn9"), e.getMessage()); // socket időtúllépés
        }
        catch (EOFException e) {
            showWarning(getString("warn_conn10"), e.getMessage()); // váratlan socket bezárás
        }
        catch (Exception e) {
            showWarning(getString("warn_conn11"), e.getMessage()); // nem várt hiba
        }
    }

    /**
     * Ha a kiválasztott Process null, fel kell dolgozni.
     * Jelzi a felhasználónak, hogy ismeretlen kérést kapott a hídszerver, majd
     * bezárja az összes többi kapcsolatot, ami már létre lett hozva az adott klienssel.
     */
    @Override
    protected void onProcessNull() {
        showWarning(getString("warn_conn12")); // ismeretlen kérés
        super.onProcessNull();
    }
    
    /**
     * Kiválasztja a biztonságos kapcsolatfeldolgozó objektumot az adatok alapján és elindítja.
     */
    @Override
    protected AbstractSecureProcess selectProcess() {
        final boolean host = getDeviceId().equals(KEY_DEV_HOST);
        final boolean controller = getDeviceId().equals(KEY_DEV_CONTROLLER);
        switch (getConnectionId()) {
            case KEY_CONN_DISCONNECT:
                if (controller) return new ControllerSideDisconnectProcess(this);
                else if (host) return new HostSideDisconnectProcess(this);
            case KEY_CONN_MESSAGE:
                if (controller) return new ControllerSideMessageProcess(this);
                else if (host) return new HostSideMessageProcess(this);
            case KEY_CONN_VIDEO_STREAM:
                if (controller) return new ControllerSideVideoProcess(this);
                else if (host) return new HostSideVideoProcess(this);
        }
        return null;
    }
    
}
