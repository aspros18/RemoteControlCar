package org.dyndns.fzoli.socket.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import org.dyndns.fzoli.socket.handler.exception.HandlerException;
import org.dyndns.fzoli.socket.handler.exception.RemoteHandlerException;

/**
 * AbstractClientHandler és AbstractServerHandler implementálásához.
 * @author zoli
 */
class HandlerUtil {
    
    /**
     * Az inicializáló metódust kivételkezelten meghívja és közli a távoli eszköznek az eredményt.
     * @throws Exception ha inicializálás közben kivétel történt
     * @throws IOException ha nem sikerült a kimenetre írni
     */
    public static void runInit(AbstractHandler handler, OutputStream out) throws IOException, Exception {
        try {
            // inicializáló metódus futtatása
            handler.init();
            // rendben jelzés küldése a távoli eszköznek
            sendStatus(out, HandlerException.VAL_OK);
        }
        catch (IOException ex) {
            // nem sikerült a rendben jelzés küldése, ezért nem próbál üzenetet küldeni
            throw ex;
        }
        catch (HandlerException ex) {
            // a kivétel üzenetét távoli eszköznek is
            sendStatus(out, ex.getMessage());
            // a kivétel megy tovább, mint ha semmi nem történt volna
            throw ex;
        }
        catch (Exception ex) {
            // olyan kivétel keletkezett, mely nem várt hiba
            sendStatus(out, "unexpected error");
            // a kivétel megy tovább...
            throw ex;
        }
    }
    
    /**
     * Megpróbálja az üzenetet elküldeni a távoli eszköznek.
     * @throws IOException ha nem sikerült a küldés
     */
    private static void sendStatus(OutputStream out, String s) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeUTF(s);
        oos.flush();
    }
    
//    private static void sendStatus(OutputStream out, String s) throws IOException {
//        PrintWriter w = new PrintWriter(out);
//        w.print(s.replace("\r", "").replace("\n", "") + "\r\n");
//        w.flush();
//    }
    
    /**
     * Megpróbálja az üzenetet fogadni a távoli géptől.
     * Ha a másik oldalon hiba keletkezett, kivételt dob.
     * @throws IOException ha nem sikerült a fogadás
     * @throws RemoteHandlerException ha a másik oldalon hiba keletkezett
     */
    public static void readStatus(InputStream in) throws IOException {
        ObjectInputStream oin = new ObjectInputStream(in);
        String status = oin.readUTF();
        if (!status.equals(HandlerException.VAL_OK)) {
            throw new RemoteHandlerException(status);
        }
    }
    
//    public static void readStatus(InputStream in) throws IOException {
//        String status = new BufferedReader(new InputStreamReader(in) {
//
//            private int count = 0;
//            
//            @Override
//            public int read(char[] cbuf, int offset, int length) throws IOException {
//                if (count++ > 1000) throw new RemoteHandlerException("long message", true);
//                return super.read(cbuf, offset, length);
//            }
//
//        }).readLine();
//        if (status != null && !status.equals(HandlerException.VAL_OK)) {
//            throw new RemoteHandlerException(status);
//        }
//    }
    
}
