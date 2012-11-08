package org.dyndns.fzoli.rccar.controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import static org.dyndns.fzoli.rccar.controller.ControllerModels.getData;
import static org.dyndns.fzoli.rccar.controller.ControllerWindows.IC_ARROWS;
import static org.dyndns.fzoli.rccar.controller.ControllerWindows.IC_CHAT;
import static org.dyndns.fzoli.rccar.controller.ControllerWindows.IC_MAP;
import org.dyndns.fzoli.rccar.controller.resource.R;

/**
 * A jármű főablaka.
 * Tartalmazza a kameraképet, a vezérlőgombokat,
 * valamint a jármű pillanatnyi sebességét és akkumulátorszintjét.
 */
public class ControllerFrame extends JFrame {

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
     * Toolbar.
     */
    private JToolBar tb;
    
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
     * Konstruktor.
     */
    public ControllerFrame() {
        initFrame();
        setComponents();
    }

    /**
     * Az ablak komponenseinek létrehozása és a felület létrehozása.
     */
    private void initFrame() {
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setIconImage(R.getIconImage());
        setLayout(new BorderLayout());
        setTitle("Főablak");
        setResizable(false);

        lbImage = new JLabel(IC_BLACK_BG); // amíg nincs MJPEG stream, fekete
        add(lbImage, BorderLayout.CENTER);

        tb = new JToolBar();
        tb.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 2));
        add(tb, BorderLayout.SOUTH); // az ablak aljára kerül a toolbar

        btControll = createButton(null, IC_CONTROLLER1, JButton.class); // vezérlés kérő gomb
        addSeparator(); // szeparátor
        btArrow = createButton("Vezérlő", IC_ARROWS, JToggleButton.class); // vezérlő ablak láthatóság szabályzó gomb
        btMap = createButton("Térkép", IC_MAP, JToggleButton.class); // radar ablak láthatóság szabályzó gomb
        btChat = createButton("Chat", IC_CHAT, JToggleButton.class); // chat ablak láthatóság szabályzó gomb
        addSeparator(); // szeparátor
        btIncrease = createButton("Növekedő sebesség", IC_INCREASE, JToggleButton.class); // chat ablak láthatóság szabályzó gomb

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
     * - A toolbar nem helyezhető át és a gomboknak nem fest ikont.
     * - Kezdetben mindhárom ablak látható, ezért az alapértelmezett érték az, hogy be vannak nyomódva a gombok.
     */
    private void setComponents() {
        tb.setFloatable(false);
        tb.setRollover(true);
        
        btArrow.setSelected(true);
        btMap.setSelected(true);
        btChat.setSelected(true);
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
            T bt = clazz.newInstance();
            tb.add(bt);
            bt.setIcon(img);
            bt.setToolTipText(text);
            bt.setFocusable(false);
            bt.setMargin(new Insets(2, 2, 2, 2));
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
        tb.addSeparator(new Dimension(8, 24));
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

}