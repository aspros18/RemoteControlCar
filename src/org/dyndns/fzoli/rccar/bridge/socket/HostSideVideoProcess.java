package org.dyndns.fzoli.rccar.bridge.socket;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.InputStream;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import net.sf.jipcam.axis.MjpegFrame;
import net.sf.jipcam.axis.MjpegInputStream;
import org.dyndns.fzoli.socket.handler.SecureHandler;
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
        final JLabel lb = new JLabel(new ImageIcon());
        final JFrame frame = new JFrame() {
            {
                setTitle("#" + getRemoteCommonName());
                add(lb);
                getContentPane().setPreferredSize(new Dimension(320, 240));
                pack();
                setLocationRelativeTo(null);
                setDefaultCloseOperation(HIDE_ON_CLOSE);
                addComponentListener(new ComponentAdapter() {

                    @Override
                    public void componentHidden(ComponentEvent e) {
                        try {
                            dispose();
                            getHandler().closeProcesses();
                        }
                        catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            }
        };
        frame.setVisible(true);
        try {
            MjpegInputStream mjpegin = new MjpegInputStream(getSocket().getInputStream());
            MjpegFrame fr;
            while((fr = mjpegin.readMjpegFrame()) != null) {
                lb.setIcon(new ImageIcon(fr.getImage()));
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            frame.dispose();
        }
    }
    
}
