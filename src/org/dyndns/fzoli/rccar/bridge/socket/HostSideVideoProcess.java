package org.dyndns.fzoli.rccar.bridge.socket;

import java.util.Date;
import net.sf.jipcam.axis.MjpegFrame;
import net.sf.jipcam.axis.MjpegInputStream;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.impl.SharedJpegProvider;
import org.dyndns.fzoli.socket.process.AbstractSecureProcess;

/**
 * A hoszt által streamelt MJPEG folyam fogadása
 * és továbbküldése képkockaként a vezérlő programoknak.
 * @author zoli
 */
public class HostSideVideoProcess extends AbstractSecureProcess {

    /**
     * Biztonságos MJPEG stream fogadó inicializálása.
     * @param handler Biztonságos kapcsolatfeldolgozó, ami létrehozza ezt az adatfeldolgozót.
     * @throws NullPointerException ha handler null
     */
    public HostSideVideoProcess(SecureHandler handler) {
        super(handler);
    }

    @Override
    public void run() {
        try {
            MjpegInputStream mjpegin = new MjpegInputStream(getSocket().getInputStream());
            MjpegFrame fr;
            while((fr = mjpegin.readMjpegFrame()) != null) {
                System.out.println("frame " + new Date());
                SharedJpegProvider.setSharedFrame(fr.getJpegBytes());
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
}
