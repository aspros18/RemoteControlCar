package org.dyndns.fzoli.rccar.controller;

import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import javax.swing.ImageIcon;
import org.dyndns.fzoli.rccar.controller.resource.R;

/**
 * A járművel kapcsolatos ablakok konténere.
 * @author zoli
 */
public class ControllerWindows {
    
    /**
     * Az ablakok felsorolása.
     * Láthatóság beállításához szükséges.
     */
    public enum WindowType {
        CONTROLL,
        CHAT,
        MAP
    }
    
    /**
     * A chatablak ikonja.
     */
    public static final ImageIcon IC_CHAT = R.getImageIcon("chat.png");

    /**
     * A vezérlőablak ikonja.
     */
    public static final ImageIcon IC_ARROWS = R.getImageIcon("arrows.png");

    /**
     * A radarablak ikonja.
     */
    public static final ImageIcon IC_MAP = R.getImageIcon("map.png");
    
    /**
     * Főablak.
     */
    private final ControllerFrame FRAME_MAIN = new ControllerFrame(this);
    
    /**
     * Chatablak.
     */
    private final ChatDialog DIALOG_CHAT = new ChatDialog(FRAME_MAIN, this);
    
    /**
     * Az ablakok pozícionálása, szükség esetén átméretezése.
     */
    public ControllerWindows() {
        //TODO: még nem teljes
        Rectangle screenSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int usedHeight = Math.min(screenSize.height, FRAME_MAIN.getHeight() + DIALOG_CHAT.getHeight());
        FRAME_MAIN.setLocation((int)(screenSize.width / 2.0 - FRAME_MAIN.getHeight() / 2.0), (int)(screenSize.height / 2.0 - usedHeight / 2.0));
        DIALOG_CHAT.setLocation(FRAME_MAIN.getLocation().x, FRAME_MAIN.getHeight() + FRAME_MAIN.getBorderSize().height);
        DIALOG_CHAT.setSize(FRAME_MAIN.getWidth(), Math.min(DIALOG_CHAT.getHeight(), screenSize.height - FRAME_MAIN.getHeight()));
    }
    
    /**
     * A megadott ablak megjelenítése/eltüntetése és esemény küldése a főablaknak.
     * @param w az ablak
     * @param b true esetén megjelenik, egyébként eltűnik
     */
    public void setVisible(WindowType w, boolean b) {
        if (w != null) {
            switch (w) {
                case CONTROLL:
                    //TODO
                    break;
                case CHAT:
                    DIALOG_CHAT.setVisible(b);
                    break;
                case MAP:
                    //TODO
            }
            FRAME_MAIN.onWindowClosed(w);
        }
    }
    
    /**
     * Az ablakok megjelenítése/eltüntetése.
     * @param b true esetén megjelenik, egyébként eltűnik
     */
    public void setVisible(boolean b) {
        FRAME_MAIN.setVisible(b);
        DIALOG_CHAT.setVisible(b);
    }
    
    /**
     * Frissíti az ablakok tartalmát a modelük alapján.
     */
    public void refresh() {
        FRAME_MAIN.refresh();
    }
    
}
