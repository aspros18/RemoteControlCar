package org.dyndns.fzoli.rccar.controller;

import javax.swing.ImageIcon;
import org.dyndns.fzoli.rccar.controller.resource.R;

/**
 * A járművel kapcsolatos ablakok konténere.
 * @author zoli
 */
public class ControllerWindows {
    
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
     * Tartalmazza a kameraképet és a vezérlőgombokat.
     */
    private final ControllerFrame MAIN_FRAME = new ControllerFrame();
    
    /**
     * Az ablakok pozícionálása.
     */
    public ControllerWindows() {
        //TODO
    }
    
    /**
     * Az ablakok megjelenítése/eltüntetése.
     */
    public void setVisible(boolean b) {
        MAIN_FRAME.setVisible(b);
    }
    
    /**
     * Frissíti az ablakok tartalmát a modelük alapján.
     */
    public void refresh() {
        MAIN_FRAME.refresh();
    }
    
}
