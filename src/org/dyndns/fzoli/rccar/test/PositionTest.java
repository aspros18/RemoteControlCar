package org.dyndns.fzoli.rccar.test;

import java.awt.Dimension;
import java.awt.Window;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 *
 * @author zoli
 */
public class PositionTest {
    
    private static void addLabel(Window win, int w, int h) {
        JLabel lb = new JLabel();
        lb.setPreferredSize(new Dimension(w, h));
        win.add(lb);
        win.pack();
    }

    public PositionTest(final boolean pre) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                JFrame fr1 = new JFrame();
                addLabel(fr1, 200, 100);
                fr1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                fr1.setLocationRelativeTo(null);
                if (pre) fr1.setVisible(true);
                JDialog d1 = new JDialog(fr1);
                addLabel(d1, 200, 50);
                if (pre) d1.setVisible(true);
                d1.setLocation(fr1.getX(), fr1.getY() + fr1.getHeight());
                JDialog d2 = new JDialog(fr1);
                addLabel(d2, 100, 100);
                d2.setLocation(fr1.getX() + fr1.getWidth(), fr1.getY());
                d2.setVisible(true);
                if (pre) return;
                fr1.setVisible(true);
                d1.setVisible(true);
            }
            
        });
    }
    
    public static void main(String[] args) throws InterruptedException {
        new PositionTest(false);
        new PositionTest(true);
        JFrame fr = new JFrame();
        fr.setLocation(-1000, -1000);
        fr.setVisible(true);
        Thread.sleep(5000);
        fr.setLocationRelativeTo(null);
    }
    
}
