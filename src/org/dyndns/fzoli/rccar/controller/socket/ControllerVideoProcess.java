package org.dyndns.fzoli.rccar.controller.socket;

import java.awt.Image;
import java.awt.image.BufferedImage;
import net.sf.jipcam.axis.MjpegFrame;
import net.sf.jipcam.axis.MjpegInputStream;
import org.dyndns.fzoli.rccar.controller.Main;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.AbstractSecureProcess;

/**
 *
 * @author zoli
 */
public class ControllerVideoProcess extends AbstractSecureProcess {

    public ControllerVideoProcess(SecureHandler handler) {
        super(handler);
    }

    @Override
    public void run() {
        try {
            MjpegInputStream mjpegin = new MjpegInputStream(getSocket().getInputStream());
            MjpegFrame fr;
            while(!getSocket().isClosed() && (fr = mjpegin.readMjpegFrame()) != null) {
                Image img = fr.getImage();
                if (img == null) continue;
                Main.setMjpegFrame((BufferedImage) img);
            }
        }
        catch (Exception ex) {
            getHandler().closeProcesses();
        }
    }
    
}
