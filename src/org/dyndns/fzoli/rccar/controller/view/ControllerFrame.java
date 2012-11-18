package org.dyndns.fzoli.rccar.controller.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import static org.dyndns.fzoli.rccar.controller.ControllerModels.getData;
import org.dyndns.fzoli.rccar.controller.ControllerWindows;
import static org.dyndns.fzoli.rccar.controller.ControllerWindows.IC_ARROWS;
import static org.dyndns.fzoli.rccar.controller.ControllerWindows.IC_CHAT;
import static org.dyndns.fzoli.rccar.controller.ControllerWindows.IC_MAP;
import org.dyndns.fzoli.rccar.controller.ControllerWindows.WindowType;
import org.dyndns.fzoli.rccar.controller.resource.R;

/**
 * A jármű főablaka.
 * Tartalmazza a kameraképet, a vezérlőgombokat,
 * valamint a jármű pillanatnyi sebességét és akkumulátorszintjét.
 */
public class ControllerFrame extends JFrame {

    /**
     * Dialógusablak láthatóság állító aktivitása.
     * Kezdetben inaktív az eseményjelzés.
     */
    private boolean dialogEvent = false;
    
    /**
     * A képkockát megjelenítő címke.
     */
    private JLabel lbImage;

    /**
     * Vezérlőgomb.
     */
    private JButton btControll;

    /**
     * Ablakmegjelenítő- és elrejtő gombok.
     */
    private JToggleButton btChat, btMap, btArrow;

    /**
     * Sebességnövekedés aktiváló/deaktiváló gomb.
     */
    private JToggleButton btIncrease;

    /**
     * Pillanatnyi sebességet mutató címke.
     */
    private JLabel lbSpeed;
    
    /**
     * Akkumulátor-szintet mutató folyamatjelző.
     */
    private JProgressBar pbAccu;
    
    /**
     * Toolbar.
     */
    private JToolBar tb;
    
    /**
     * Oszlopszámláló az elrendezés-menedzser megszorításához.
     */
    private int colCounter = 0;
    
    /**
     * Vezérlőgomb ikonja, amikor átadható a vezérlés.
     */
    private static final ImageIcon IC_CONTROLLER1 = R.getImageIcon("controller1.png");

    /**
     * Vezérlőgomb ikonja, amikor kérhető a vezérlés.
     */
    private static final ImageIcon IC_CONTROLLER2 = R.getImageIcon("controller2.png");

    /**
     * Növekedést jelző ikon.
     */
    private static final ImageIcon IC_INCREASE = R.getImageIcon("increase.png");

    /**
     * Teljesen fekete képkocka.
     */
    private static final ImageIcon IC_BLACK_BG = new ImageIcon(new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB) {
        {
            Graphics g = getGraphics();
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    });
    
    /**
     * A járműhöz tartozó ablakok konténere.
     */
    public final ControllerWindows WINDOWS;
    
    /**
     * Konstruktor.
     */
    public ControllerFrame(ControllerWindows windows) {
        WINDOWS = windows;
        initFrame();
        setComponents();
    }

    /**
     * Az ablak komponenseinek létrehozása és a felület létrehozása.
     */
    private void initFrame() {
        setResizable(false);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setIconImage(R.getIconImage());
        setLayout(new BorderLayout());
        setTitle("Főablak");
        
        lbImage = new JLabel(IC_BLACK_BG); // amíg nincs MJPEG stream, fekete
        add(lbImage, BorderLayout.CENTER);

        tb = new JToolBar();
        tb.setLayout(new GridBagLayout());
        add(tb, BorderLayout.SOUTH); // az ablak aljára kerül a toolbar

        btControll = createButton(null, IC_CONTROLLER1, JButton.class); // vezérlés kérő gomb
        addSeparator(); // szeparátor
        btMap = createButton("Térkép", IC_MAP, JToggleButton.class); // radar ablak láthatóság szabályzó gomb
        btArrow = createButton("Vezérlő", IC_ARROWS, JToggleButton.class); // vezérlő ablak láthatóság szabályzó gomb
        btChat = createButton("Chat", IC_CHAT, JToggleButton.class); // chat ablak láthatóság szabályzó gomb
        addSeparator(); // szeparátor
        btIncrease = createButton("Növekedő sebesség", IC_INCREASE, JToggleButton.class); // chat ablak láthatóság szabályzó gomb

        JPanel pStat = new JPanel(); // a statisztika panel ...
        pStat.setOpaque(false); // ... átlátszó és ...
        pStat.setLayout(new FlowLayout(FlowLayout.RIGHT, 8, 0)); // ... jobbra igazítva kerülnek rá a komponensek 8 pixel hézaggal
        GridBagConstraints c = getGbc();
        c.fill = GridBagConstraints.HORIZONTAL; // a panel magasságban minimális, hogy a toolbar közepén legyen
        c.weightx = Integer.MAX_VALUE; // a panel a maradék hely teljes kitöltésével ...
        tb.add(pStat, c); // ... hozzáadódik a toolbarhoz, mint utolsó komponens
        
        lbSpeed = new JLabel("Sebesség: 20 km/h"); // TODO: tesztszöveg törlése
        pStat.add(lbSpeed); // sebesség kijelző inicializálása, hozzáadás az ablakhoz
        
        pbAccu = new JProgressBar(); // akkumulátor-szint kijelző inicializálása
        pbAccu.setString("Akku: 100%"); // TODO: tesztszöveg törlése
        pbAccu.setStringPainted(true); // a beállított szöveg jelenjen meg
        pbAccu.setValue(100); // TODO: tesztsor törlése
        pStat.add(pbAccu); // hozzáadás az ablakhoz
        
        pack(); // ablak méretének optimalizálása
        
        addWindowListener(new WindowAdapter() {

            /**
             * A főablak bezárásakor nem lesz kiválasztva jármű.
             * Amikor a szerver megkapja, hogy nincs jármű kiválasztva,
             * elküldi a teljes jármű listát és a kliens megjeleníti a
             * járműválasztó ablakot újra.
             */
            @Override
            public void windowClosing(WindowEvent e) {
                getData().setHostName(null);
            }

        });
    }
    
    /**
     * A komponensek alapértelmezéseinek beállítása.
     * - A toolbar nem helyezhető át és a gomboknak nem fest szegélyt, míg nem kerül egér föléjük.
     * - A három másik dialógusablakhoz tartozik három gomb és rájuk kattintva a hozzájuk tartozó ablak jelenk meg vagy tűnik el.
     */
    private void setComponents() {
        tb.setFloatable(false);
        tb.setRollover(true);
        
        ChangeListener clDialogs = new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                if (WINDOWS != null) {
                    if (!dialogEvent) return;
                    WindowType window = null;
                    JToggleButton src = (JToggleButton) e.getSource();
                    if (src == btArrow) window = WindowType.CONTROLL;
                    else if (src == btChat) window = WindowType.CHAT;
                    else if (src == btMap) window = WindowType.MAP;
                    WINDOWS.setVisible(window, src.isSelected());
                }
            }
                           
        };
        
        btArrow.addChangeListener(clDialogs);
        btChat.addChangeListener(clDialogs);
        btMap.addChangeListener(clDialogs);
        
    }
    
    /**
     * A toolbar elrendezés-menedzserének a megszorítása.
     */
    private GridBagConstraints getGbc() {
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(2, 2, 2, 2);
        c.gridx = colCounter;
        colCounter++;
        return c;
    }
    
    /**
     * Panelhez gyárt gombot.
     * A panelen lévő gombok nem fókuszálhatóak.
     * @param tb a panel, amihez hozzáadódik a gomb
     * @param text a gomb tooltip szövege
     * @param img a gomb ikonja
     * @param clazz a gomb típusa
     */
    private <T extends AbstractButton> T createButton(String text, ImageIcon img, Class<T> clazz) {
        try {
            GridBagConstraints c = getGbc();
            T bt = clazz.newInstance();
            tb.add(bt, c);
            bt.setIcon(img);
            bt.setToolTipText(text);
            bt.setFocusable(false);
            bt.setMargin(c.insets);
            return bt;
        }
        catch (Exception ex) {
            return null;
        }
    }

    /**
     * Szeparátor hozzáadása a toolbarhoz.
     */
    private void addSeparator() {
        tb.add(new JSeparator(SwingConstants.VERTICAL), getGbc());
    }
    
    /**
     * Frissíti az ablak tartalmát a model alapján.
     * Az alábbi táblázat alapján XNOR művelet dönti el, hogy aktív-e a gomb
     * és az első opció dönti el az ikon típusát:
     *    vezérli? akarja?  esemény
     *    i        i        lemondás aktív
     *    h        i        kérés inaktív
     *    i        h        lemondás inaktív
     *    h        h        kérés aktív
     */
    public void refresh() {
        btControll.setIcon(getData().isControlling() ? IC_CONTROLLER1 : IC_CONTROLLER2);
        btControll.setToolTipText(getData().isControlling() ? "Vezérlés átadása" : "Vezérlés kérése");
        btControll.setEnabled(!(getData().isControlling() ^ getData().isWantControl()));
    }

//    /**
//     * Megadja az ablak keretének méretét.
//     * A többi ablak kezdeti pozíciójának beállításához ismerni kell az ablakkeret méretét.
//     */
//    public Dimension getBorderSize() {
//        Dimension fullSize = getSize();
//        Dimension paneSize = getContentPane().getSize();
//        return new Dimension(fullSize.width - paneSize.width, fullSize.height - paneSize.height);
//    }
    
    /**
     * Megadja, hogy az ablakhoz tartozó gomb be van-e nyomódva.
     * Ha nincs paraméter megadva, false.
     * @param w az ablak
     */
    public boolean getWindowVisibility(WindowType w) {
        JToggleButton bt = getButton(w);
        if (bt != null) return bt.isSelected();
        return false;
    }
    
    /**
     * Ha valamelyik dialógus megjelenik vagy bezárult, a gombok frissítése.
     * Az alábbi esetekben hívódik meg:
     * - Kezdetben egyes dialógusok láthatók, mások meg nem.
     * - Ha a felhasználó bezárja az egyik ablakot, a hozzá tartozó gomb kijelölését le kell venni.
     * @param w az ablak
     * @param visible true esetén kiválasztódik a gomb
     */
    public void setWindowVisibility(WindowType w, boolean visible) {
        JToggleButton bt = getButton(w);
        if (bt != null) bt.setSelected(visible);
    }
    
    /**
     * Dialógusablakokat megjelenítő/elrejtő esemény aktiválása/inaktiválása.
     */
    public void setDialogEvent(boolean active) {
        dialogEvent = active;
    }
    
    /**
     * Az ablakhoz tartozó gombot adja vissza.
     * @param w az ablak
     * @return ha a paraméter nem null az ablakhoz tartozó gomb, egyébkénz null
     */
    private JToggleButton getButton(WindowType w) {
        if (w != null) {
            switch (w) {
                case CONTROLL:
                    return btArrow;
                case CHAT:
                    return btChat;
                case MAP:
                    return btMap;
            }
        }
        return null;
    }
    
}
