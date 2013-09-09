package org.dyndns.fzoli.rccar.socket;

import java.io.InputStream;
import java.io.OutputStream;
import org.dyndns.fzoli.rccar.ConnectionKeys;
import org.dyndns.fzoli.socket.handler.BufferedStreamDeviceHandler;
import org.dyndns.fzoli.socket.handler.DeviceHandler;
import org.dyndns.fzoli.socket.handler.ObjectStreamDeviceHandler;
import org.dyndns.fzoli.socket.stream.JsonStreamMethod;
import org.dyndns.fzoli.socket.stream.ObjectStreamMethod;
import org.dyndns.fzoli.socket.stream.StreamMethod;

/**
 * A szerver és a kliensek közti kommunikáció módjának definiálása.
 * @author zoli
 */
public class CommunicationMethodChooser implements ConnectionKeys {
    
    /**
     * A {@code MessageProcess} üzenetküldő- és fogadó I/O folyamait kezelő objektum legyártása.
     * Szöveg alapú kommunikáció esetén JSON formátumban kerülnek küldésre az objektumok,
     * egyébként a Java ObjectInputStream és ObjectOutputStream használatával.
     */
    public static StreamMethod createStreamMethod(int deviceId) {
        if (isPure(deviceId)) return new JsonStreamMethod();
        else return new ObjectStreamMethod();
    }
    
    /**
     * A {@code Handler} osztályok státuszüzenet küldését és fogadását elvégző objektum legyártása.
     * Szöveg alapú kommunikáció esetén nyers szövegként kerül küldésre a szöveg újsorjellel a végén,
     * egyébként a Java ObjectInputStream és ObjectOutputStream használatával.
     */
    public static DeviceHandler createDeviceHandler(int deviceId, InputStream in, OutputStream out) {
        if (isPure(deviceId)) return new BufferedStreamDeviceHandler(in, out);
        else return new ObjectStreamDeviceHandler(in, out);
    }
    
    /**
     * Megadja, hogy az adott eszközazonosító tiszta, szöveg alapú kommunikációt igényel-e.
     */
    private static boolean isPure(int deviceId) {
        return deviceId == KEY_DEV_PURE_CONTROLLER || deviceId == KEY_DEV_PURE_HOST;
    }
    
}
