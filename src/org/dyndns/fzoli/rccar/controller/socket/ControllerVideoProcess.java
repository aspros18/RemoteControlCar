package org.dyndns.fzoli.rccar.controller.socket;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import net.sf.jipcam.axis.MjpegFrame;
import net.sf.jipcam.axis.MjpegInputStream;
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
        final JLabel lb = new JLabel(new ImageIcon());
        final JFrame frame = new JFrame() {
            {
                setTitle("Camera test");
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
