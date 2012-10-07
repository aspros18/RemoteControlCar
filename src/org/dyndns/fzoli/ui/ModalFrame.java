package org.dyndns.fzoli.ui;

import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import javax.swing.JFrame;

/**
 * Egy JFrame, ami képes szálon belül modális dialógusablakként funkcionálni.
 * @author zoli
 */
public class ModalFrame extends JFrame {
    
    /**
     * Alapértelmezetten modális.
     */
    private boolean modal = true;

    /**
     * Segédváltozó, hogy két {@code setVisible(true)} ne indítson két ciklust.
     */
    private boolean blocking = false;
    
    public ModalFrame() throws HeadlessException {
        super();
    }

    public ModalFrame(GraphicsConfiguration gc) {
        super(gc);
    }

    public ModalFrame(String title) throws HeadlessException {
        super(title);
    }

    public ModalFrame(String title, GraphicsConfiguration gc) {
        super(title, gc);
    }

    /**
     * Megadja, hogy modálisan viselkedik-e az ablak.
     */
    public boolean isModal() {
        return modal;
    }

    /**
     * Beállítja az ablak modalitását.
     */
    public void setModal(boolean modal) {
        this.modal = modal;
    }

    /**
     * Megjeleníti vagy elrejti az ablakot.
     * Ha modális az ablak, megjelenés után
     * addig blokkolja a szálat, míg látható az ablak.
     */
    @Override
    public void setVisible(boolean b) {
        if (isVisible() ^ b) super.setVisible(b);
        if (b) {
            toFront();
            repaint();
        }
        if (!blocking && b) {
            blocking = true;
            while (isModal() && isVisible()) {
                try {
                    Thread.sleep(5);
                }
                catch (InterruptedException ex) {
                    ;
                }
            }
            blocking = false;
        }
    }
    
}
