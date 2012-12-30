package org.dyndns.fzoli.rccar.controller;

import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Window;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
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
            radar.setPosition(47.35021, 19.10236, 200.53);
        }
        
    });
    
    /**
     * Hézag az ablakok között.
     * Az alábbi okok miatt van rá szükségem:
     * - Windows alatt amikor az Aero téma használatban van, az ablakok elrendezése másként funkcionál, mint Aero nélkül.
     *   Aero nélkül pixelre pontosan jó helyre kerülnek az ablakok, egyébként kicsit egymásra elcsúsznak.
     *   Ezt az egymásra csúszást kerülöm el azzal, hogy hézagot hagyok az ablakok körül.
     *   Ez a hézag a csetablak esetén csak a fele, hogy elkerüljem azt is, hogy a 0. magasság koordináta ne menjen a képernyő fölé,
     *   ezzel levágva a két felső ablak címsorából egy kevesett. A csetablak mérete és a hézag felével csökkentett, így alatta is van egy kis hézag.
     * - Linux alatt GNOME felület esetén szintén másképpen működik az ablakok pozícionálása és ezért sem lehet egyértelműen egymás mellé
     *   tenni az ablakokat. Arról nem is beszélve, hogy XFCE alatt alapban van egy kis hézag az ablakokra hagyva.
     * - Ha az ablakok kezdetben láthatóak lennének, akkor pontos ablakméretet lehetne elkérni, de én még a megjelenés előtt beállítom a pozíciót.
     *   Ha az ablak nem látható, nem helyezhető tálcára, így még ezt a trükköt sem alkalmazhatom, valamint negatív koordinátát sem fogad el
     *   minden grafikus felület, tehát nem lehet megoldani, hogy az ablak kirajzolódjon, de ne legyen látható.
     * A konstans értéke: 10 pixel (optimális érték, nem ajánlatos elállítani)
     */
    private final static int GAP = 10;
    
    /**
     * Az ablakok pozícionálása, szükség esetén átméretezése.
     */
    public ControllerWindows() {
        final Rectangle SCREEN_SIZE = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int emptyHeight = SCREEN_SIZE.height - (FRAME_MAIN.getHeight() + DIALOG_CHAT.getHeight() + GAP); // maradék magasság a két ablakkal együtt
        
        if (emptyHeight < 0) { // ha nem fér ki a főablak és a chatablak (a két ablak) magasságilag
            emptyHeight = SCREEN_SIZE.height - FRAME_MAIN.getHeight() - GAP; // maradék magasság csak a főablakkal
            if (emptyHeight >= DIALOG_CHAT.getMinimumSize().height) { // ha a maradék magasság elég nagy a chatablaknak
                DIALOG_CHAT.setSize(DIALOG_CHAT.getWidth(), emptyHeight - GAP); // magasság beállítása teljes helykitöltésre
                emptyHeight = 0; // az átméretezéssel a maradék hely pontosan nulla lett
            }
            else {
                emptyHeight = -1; // nem elég a képernyő felbontása magasságban
            }
        }
        
        final Window BIGGER_WINDOW = DIALOG_MAP.getWidth() >= DIALOG_MAP.getWidth() ? DIALOG_MAP : DIALOG_MAP; // megnézi, hogy a térkép vagy a vezérlő dialógus a nagyobb
        int emptyWidth = SCREEN_SIZE.width - (FRAME_MAIN.getWidth() + BIGGER_WINDOW.getWidth()) - GAP; // maradék szélesség a két ablakkal együtt
        
        boolean isEmptyWidth = emptyWidth >= 0; // van-e szabad hely a képernyőn szélességében ...
        boolean isEmptyHeight = emptyHeight >= 0; // ... és magasságában
        
        // Főablak pozíciójának beállítása: ha a maradék szélesség negatív, szélességileg egyedül kerül a képernyő közepére, egyébként a másik ablakkal együtt kerül középre. Magasságilag ugyan ez a logika, csak a csetablakkal és hézag nélkül, hogy csak a fele térköz legyen meg
        // A kezdeti pozícióhoz hozzáadódik az ablakterület kezdetének koordinátája is, így pl. ha Windows rendszeren a start menü felül van, a főablak teteje nem fog belelógni
        FRAME_MAIN.setLocation((SCREEN_SIZE.width / 2) - ((FRAME_MAIN.getWidth() + (!isEmptyWidth ? 0 : BIGGER_WINDOW.getWidth() + GAP)) / 2) + SCREEN_SIZE.x, (SCREEN_SIZE.height / 2) - ((FRAME_MAIN.getHeight() + (!isEmptyHeight ? 0 : DIALOG_CHAT.getHeight())) / 2) + SCREEN_SIZE.y);
        
        // térkép- és vezérlőablak láthatóságának beállítása: ha elférnek a főablak mellett, akkor kezdetben láthatóak, egyébként meg nem
        FRAME_MAIN.setWindowVisibility(ControllerWindows.WindowType.MAP, isEmptyWidth);
        FRAME_MAIN.setWindowVisibility(ControllerWindows.WindowType.CONTROLL, isEmptyWidth);
        
        // térkép- és vezérlőablak kezdőpozíciója: ha elférnek a főablak mellett, akkor a főablak mellé (jobb oldalra) kerülnek, egyébként meg a főablak jobb széléhez lesznek igazítva
        DIALOG_MAP.setLocation(FRAME_MAIN.getX() + FRAME_MAIN.getWidth() - (isEmptyWidth ? -1 * GAP : DIALOG_MAP.getWidth()), FRAME_MAIN.getY());
        DIALOG_ARROWS.setLocation(DIALOG_MAP.getX(), DIALOG_MAP.getY() + DIALOG_MAP.getHeight() + GAP); // a térkép alá kerül a vezérlő ablak
        
        // chatablak láthatóságának beállítása: akkor látható, ha kifér a főablakkal együtt
        FRAME_MAIN.setWindowVisibility(ControllerWindows.WindowType.CHAT, isEmptyHeight);
        
        // chatablak pozíciójának beállítása: ha kifér a főablakkal együtt, akkor a főablak alá, egyébként a főablak gombsora fölé
        DIALOG_CHAT.setLocation(FRAME_MAIN.getX(), FRAME_MAIN.getY() + FRAME_MAIN.getHeight() - (isEmptyHeight ? -1 * GAP / 2 : DIALOG_CHAT.getHeight() + FRAME_MAIN.getToolBarHeight() + (GAP / 2)));
    }
    
    /**
     * A megadott ablak megjelenítése/eltüntetése és esemény küldése a főablaknak.
     * @param w az ablak
     * @param b true esetén megjelenik, egyébként eltűnik
     */
    public void setVisible(ControllerWindows.WindowType w, boolean b) {
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
            if (FRAME_MAIN.getWindowVisibility(ControllerWindows.WindowType.CHAT)) setVisible(DIALOG_CHAT, true);
            if (FRAME_MAIN.getWindowVisibility(ControllerWindows.WindowType.MAP)) setVisible(DIALOG_MAP, true);
            if (FRAME_MAIN.getWindowVisibility(ControllerWindows.WindowType.CONTROLL)) setVisible(DIALOG_ARROWS, true);
            FRAME_MAIN.requestFocus();
        }
        else {
            setVisible(DIALOG_CHAT, false);
            setVisible(DIALOG_MAP, false);
            setVisible(DIALOG_ARROWS, false);
        }
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
                ControllerWindows win = new ControllerWindows();
                win.FRAME_MAIN.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                win.setVisible(true);
            }
            
        });
        NativeInterface.runEventPump();
    }
    
}
