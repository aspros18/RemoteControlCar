package org.dyndns.fzoli.rccar.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import javax.net.ssl.SSLSocket;
import org.dyndns.fzoli.rccar.ConnectionKeys;
import org.dyndns.fzoli.socket.handler.AbstractSecureClientHandler;
import org.dyndns.fzoli.socket.handler.exception.HandlerException;
import org.dyndns.fzoli.socket.handler.exception.RemoteHandlerException;

/**
 * ObjectStream osztályt felváltó readLine() alapú megoldás.
 * C++ teszt része.
 * @author zoli
 */
public abstract class RCHandler extends AbstractSecureClientHandler {

    public RCHandler(SSLSocket socket, int connectionId) {
        super(socket, ConnectionKeys.KEY_DEV_CONTROLLER, connectionId);
    }
    
    @Override
    public void runInit(OutputStream out) throws IOException, Exception {
        try {
            // inicializáló metódus futtatása
            init();
            // rendben jelzés küldése a távoli eszköznek
            sendStatus(out, HandlerException.VAL_OK);
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

    private void sendStatus(OutputStream out, String s) throws IOException {
        out.write((s.replace("\r", "").replace("\n", "") + "\r\n").getBytes());
        out.flush();
    }

    @Override
    protected void readStatus(InputStream in) throws IOException {
        String status = new BufferedReader(new InputStreamReader(in) {

            private int count = 0;

            @Override
            public int read(char[] cbuf, int offset, int length) throws IOException {
                if (count >= 100) throw new RemoteHandlerException("long message", true);
                int bytes = super.read(cbuf, offset, length);
                count += bytes;
                return bytes;
            }

        }, 100).readLine();
        if (status != null && !status.equals(HandlerException.VAL_OK)) {
            throw new RemoteHandlerException(status);
        }
    }
    
}
