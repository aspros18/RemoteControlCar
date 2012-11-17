package org.dyndns.fzoli.rccar.controller;

import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Window;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import org.dyndns.fzoli.rccar.controller.resource.R;
import org.dyndns.fzoli.rccar.controller.view.ChatDialog;
import org.dyndns.fzoli.rccar.controller.view.ControllerFrame;
import org.dyndns.fzoli.rccar.controller.view.map.MapDialog;
import org.dyndns.fzoli.rccar.controller.view.map.MapLoadListener;
import org.dyndns.fzoli.ui.UIUtil;

/**
 * A járművel kapcsolatos ablakok konténere.
 * TODO: a program indulásakor ne jelenjenek meg az ablakok!
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
        //TODO: még nem teljes
        final Rectangle SCREEN_SIZE = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int emptyHeight = SCREEN_SIZE.height - (FRAME_MAIN.getHeight() + DIALOG_CHAT.getHeight()); // maradék magasság a két ablakkal együtt
        if (emptyHeight < 0) { // ha nem fér ki a főablak és a chatablak (a két ablak) magasságilag
            emptyHeight = SCREEN_SIZE.height - FRAME_MAIN.getHeight(); // maradék magasság csak a főablakkal
            if (emptyHeight >= DIALOG_CHAT.getMinimumSize().height) { // ha a maradék magasság elég nagy a chatablaknak
                DIALOG_CHAT.setSize(DIALOG_CHAT.getWidth(), emptyHeight); // magasság beállítása teljes helykitöltésre
                emptyHeight = 0; // az átméretezéssel a maradék hely pontosan nulla lett
            }
        }
        final Window BIGGER_WINDOW = DIALOG_MAP.getWidth() >= DIALOG_MAP.getWidth() ? DIALOG_MAP : DIALOG_MAP; // megnézi, hogy a térkép vagy a vezérlő dialógus a nagyobb
        int emptyWidth = SCREEN_SIZE.width - (FRAME_MAIN.getWidth() + BIGGER_WINDOW.getWidth()); // maradék szélesség a két ablakkal együtt
        // főablak pozíciójának beállítása: ha a maradék szélesség negatív, szélességileg a képernyő közepére egyedül, egyébként a másik ablakkal. magasságilag ugyan ez a logika, csak a csetablakkal
        FRAME_MAIN.setLocation((SCREEN_SIZE.width / 2) - ((FRAME_MAIN.getWidth() + (emptyWidth < 0 ? 0 : BIGGER_WINDOW.getWidth())) / 2), (SCREEN_SIZE.height / 2) - ((FRAME_MAIN.getHeight() + (emptyHeight < 0 ? 0 : DIALOG_CHAT.getHeight())) / 2));
        if (emptyWidth < 0) { // ha nem fér ki a két ablak szélességileg
            // mindkét dialógus (térkép és vezérlő) kezdetben láthatatlan és a főablak jobb felső sarka felé húznak
            FRAME_MAIN.setWindowVisibility(WindowType.MAP, false);
            FRAME_MAIN.setWindowVisibility(WindowType.CONTROLL, false);
        }
        else {
            // mindkét dialógus kezdetben látható és a főablaktól jobbra helyezkednek el
            FRAME_MAIN.setWindowVisibility(WindowType.MAP, true);
            FRAME_MAIN.setWindowVisibility(WindowType.CONTROLL, true);
        }
        if (emptyHeight < 0) { // ha nem fér ki a főablak és a chatablak
            // a chatablak kezdetben nem látható ...
            // ... és a főablak jobb alsó sarkában végződik
            FRAME_MAIN.setWindowVisibility(WindowType.CHAT, false);
        }
        else {
            // a chatablak kezdetben látható és a főablak alatt helyezkedik el
            FRAME_MAIN.setWindowVisibility(WindowType.CHAT, true);
            DIALOG_CHAT.setLocation(FRAME_MAIN.getX(), FRAME_MAIN.getHeight() + FRAME_MAIN.getBorderSize().height);
        }
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
        }
        else {
            setVisible(DIALOG_CHAT, false);
            setVisible(DIALOG_MAP, false);
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
