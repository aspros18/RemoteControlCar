package org.dyndns.fzoli.rccar.controller.socket;

import java.awt.Image;
import java.awt.image.BufferedImage;
import org.dyndns.fzoli.socket.mjpeg.jipcam.MjpegFrame;
import org.dyndns.fzoli.socket.mjpeg.jipcam.MjpegFrameReader;
import org.dyndns.fzoli.rccar.controller.Main;
import org.dyndns.fzoli.rccar.socket.AbstractVideoProcess;
import org.dyndns.fzoli.socket.handler.SecureHandler;

/**
 * A vezérlő oldal MJPEG-folyam feldolgozója.
 * @author zoli
 */
public class ControllerVideoProcess extends AbstractVideoProcess {

    public ControllerVideoProcess(SecureHandler handler) {
        super(handler);
    }

    /**
     * A kiolvasott képkockát megjeleníti a főablakon.
     */
    @Override
    protected void processFrame(MjpegFrame fr) throws Exception {
        if (fr == null) return;
        Image img = MjpegFrameReader.getImage(fr);
        if (img == null) return;
        Main.setMjpegFrame((BufferedImage) img);
    }

    /**
     * Ha hiba keletkezik, a kapcsolat bezárul.
     * A kliens oldalon le kell zárni a többi kapcsolatot is, mert csak akkor van értelme a programnak,
     * ha mindegyik kapcsolat aktív.
     */
    @Override
    protected void onException(Exception ex) {
        getHandler().closeProcesses();
    }
    
}
