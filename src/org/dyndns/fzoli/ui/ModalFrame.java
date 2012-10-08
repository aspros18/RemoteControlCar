package org.dyndns.fzoli.ui;

import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import javax.swing.JFrame;

/**
 * Modális dialógus szerű frame.
 * @author zoli
 */
public class ModalFrame extends JFrame {

    /**
     * Alapértelmezetten modálisan viselkedik.
     */
    private boolean modal = true;
    
    public ModalFrame() throws HeadlessException {
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
     * Megmondja, modális-e az ablak.
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
     * Ha az ablak modális, a metódus nem ér véget addig, míg az ablak látható.
     * @param b true esetén megjelenik, egyébként eltűnik az ablak
     */
    @Override
    public void setVisible(boolean b) {
        super.setVisible(b); // megjelenik vagy elrejtődik, aztán ...
        if (b && isModal()) { // ... ha megjelenést kértek és jelenleg modális ...
            while (isVisible()) { // ... ciklus, amíg látható az ablak
                try {
                    Thread.sleep(5); // processzor pihentetés nagyon hosszú 5 ezredmásodpercre
                }
                catch (InterruptedException ex) {
                    ;
                }
            }
        }
    }

    /**
     * Előtérbe hozza az ablakot akkor is, ha az le van csukva a talcára.
     */
    @Override
    public void toFront() {
        int state = super.getExtendedState();
        state &= ~ICONIFIED;
        super.setExtendedState(state);
        super.setAlwaysOnTop(true);
        super.toFront();
        super.requestFocus();
        super.setAlwaysOnTop(false);
    }
    
}
