package org.dyndns.fzoli.rccar.controller;

import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Window;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import org.dyndns.fzoli.rccar.controller.resource.R;
import org.dyndns.fzoli.rccar.controller.view.ArrowDialog;
import org.dyndns.fzoli.rccar.controller.view.ChatDialog;
import org.dyndns.fzoli.rccar.controller.view.ControllerFrame;
import org.dyndns.fzoli.rccar.controller.view.map.MapDialog;
import org.dyndns.fzoli.rccar.controller.view.map.MapLoadListener;
import org.dyndns.fzoli.ui.UIUtil;

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
     * Vezérlőablak.
     */
    private final ArrowDialog DIALOG_ARROWS = new ArrowDialog(FRAME_MAIN, this);
    
    /**
     * Térkép dialógus.
     */
    private final MapDialog DIALOG_MAP = new MapDialog(FRAME_MAIN, this, new MapLoadListener() {

        @Override
        public void loadFinished(MapDialog radar) {
            //TESZT:
            radar.setArrow(null);
            radar.setPosition(47.35021, 19.10236);
        }
        
    });
    
    /**
     * Az ablakok pozícionálása, szükség esetén átméretezése.
     */
    public ControllerWindows() {
        final Rectangle SCREEN_SIZE = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int emptyHeight = SCREEN_SIZE.height - (FRAME_MAIN.getHeight() + DIALOG_CHAT.getHeight()); // maradék magasság a két ablakkal együtt
        
        if (emptyHeight < 0) { // ha nem fér ki a főablak és a chatablak (a két ablak) magasságilag
            emptyHeight = SCREEN_SIZE.height - FRAME_MAIN.getHeight(); // maradék magasság csak a főablakkal
            if (emptyHeight >= DIALOG_CHAT.getMinimumSize().height) { // ha a maradék magasság elég nagy a chatablaknak
                DIALOG_CHAT.setSize(DIALOG_CHAT.getWidth(), emptyHeight); // magasság beállítása teljes helykitöltésre
                emptyHeight = 0; // az átméretezéssel a maradék hely pontosan nulla lett
            }
            else {
                emptyHeight = -1; // nem elég a képernyő felbontása magasságban
            }
        }
        
        final Window BIGGER_WINDOW = DIALOG_MAP.getWidth() >= DIALOG_MAP.getWidth() ? DIALOG_MAP : DIALOG_MAP; // megnézi, hogy a térkép vagy a vezérlő dialógus a nagyobb
        int emptyWidth = SCREEN_SIZE.width - (FRAME_MAIN.getWidth() + BIGGER_WINDOW.getWidth()); // maradék szélesség a két ablakkal együtt
        
        boolean isEmptyWidth = emptyWidth >= 0; // van-e szabad hely a képernyőn szélességében ...
        boolean isEmptyHeight = emptyHeight >= 0; // ... és magasságában
        
        // főablak pozíciójának beállítása: ha a maradék szélesség negatív, szélességileg a képernyő közepére egyedül, egyébként a másik ablakkal. magasságilag ugyan ez a logika, csak a csetablakkal
        FRAME_MAIN.setLocation((SCREEN_SIZE.width / 2) - ((FRAME_MAIN.getWidth() + (!isEmptyWidth ? 0 : BIGGER_WINDOW.getWidth())) / 2), (SCREEN_SIZE.height / 2) - ((FRAME_MAIN.getHeight() + (!isEmptyHeight ? 0 : DIALOG_CHAT.getHeight())) / 2));
        
        // térkép- és vezérlőablak láthatóságának beállítása: ha elférnek a főablak mellett, akkor kezdetben láthatóak, egyébként meg nem
        FRAME_MAIN.setWindowVisibility(WindowType.MAP, isEmptyWidth);
        FRAME_MAIN.setWindowVisibility(WindowType.CONTROLL, isEmptyWidth);
        
        // térkép- és vezérlőablak kezdőpozíciója: ha elférnek a főablak mellett, akkor a főablak mellé (jobb oldalra) kerülnek, egyébként meg a főablak jobb széléhez lesznek igazítva
        DIALOG_MAP.setLocation(FRAME_MAIN.getX() + FRAME_MAIN.getWidth() - (isEmptyWidth ? 0 : DIALOG_MAP.getWidth()), FRAME_MAIN.getY());
        DIALOG_ARROWS.setLocation(DIALOG_MAP.getX(), DIALOG_MAP.getY() + DIALOG_MAP.getHeight()); // a térkép alá kerül a vezérlő ablak
        
        // chatablak láthatóságának beállítása: akkor látható, ha kifér a főablakkal együtt
        FRAME_MAIN.setWindowVisibility(WindowType.CHAT, isEmptyHeight);
        
        // chatablak pozíciójának beállítása: ha kifér a főablakkal együtt, akkor a főablak alá, egyébként a főablak aljára
        DIALOG_CHAT.setLocation(FRAME_MAIN.getX(), FRAME_MAIN.getY() + FRAME_MAIN.getHeight() - (isEmptyHeight ? 0 : DIALOG_CHAT.getHeight()));
        FRAME_MAIN.setDialogEvent(true); // eseménykezelő aktiválása
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
                    DIALOG_ARROWS.setVisible(b);
                    break;
                case CHAT:
                    DIALOG_CHAT.setVisible(b);
                    break;
                case MAP:
                    DIALOG_MAP.setVisible(b);
            }
            if (!b) FRAME_MAIN.setWindowVisibility(w, false);
        }
    }
    
    /**
     * Az ablakok megjelenítése/eltüntetése.
     * Csak akkor jelennek meg a dialógusok, ha a főablakon a gombjuk kijelölt.
     * @param b true esetén megjelenik, egyébként eltűnik
     */
    public void setVisible(boolean b) {
        setVisible(FRAME_MAIN, b);
        if (b) {
            if (FRAME_MAIN.getWindowVisibility(WindowType.CHAT)) setVisible(DIALOG_CHAT, true);
            if (FRAME_MAIN.getWindowVisibility(WindowType.MAP)) setVisible(DIALOG_MAP, true);
            if (FRAME_MAIN.getWindowVisibility(WindowType.CONTROLL)) setVisible(DIALOG_ARROWS, true);
        }
        else {
            setVisible(DIALOG_CHAT, false);
            setVisible(DIALOG_MAP, false);
            setVisible(DIALOG_ARROWS, false);
        }
        FRAME_MAIN.requestFocus();
    }
    
    /**
     * Az ablak láthatóságát módosítja, ha még nem változott.
     * @param w az ablak
     * @param b true esetén megjelenik, egyébként eltűnik
     */
    private static void setVisible(Window w, boolean b) {
        if (w.isVisible() ^ b) {
            w.setVisible(b);
        }
    }
    
    /**
     * Frissíti az ablakok tartalmát a modelük alapján.
     */
    public void refresh() {
        FRAME_MAIN.refresh();
    }
    
    public static void main(String[] args) {
        NativeInterface.open();
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                UIUtil.setSystemLookAndFeel();
                new ControllerWindows().setVisible(true);
            }
            
        });
        NativeInterface.runEventPump();
    }
    
}
