package org.dyndns.fzoli.rccar.controller.socket;

import java.awt.Image;
import java.awt.image.BufferedImage;
import net.sf.jipcam.axis.MjpegFrame;
import org.dyndns.fzoli.rccar.controller.Main;
import org.dyndns.fzoli.rccar.socket.AbstractVideoProcess;
import org.dyndns.fzoli.socket.handler.SecureHandler;

/**
 *
 * @author zoli
 */
public class ControllerVideoProcess extends AbstractVideoProcess {

    public ControllerVideoProcess(SecureHandler handler) {
        super(handler);
    }

    @Override
    protected void processFrame(MjpegFrame fr) {
        if (fr == null) return;
        Image img = fr.getImage();
        if (img == null) return;
        Main.setMjpegFrame((BufferedImage) img);
    }

    @Override
    protected void onException(Exception ex) {
        getHandler().closeProcesses();
    }
    
}
