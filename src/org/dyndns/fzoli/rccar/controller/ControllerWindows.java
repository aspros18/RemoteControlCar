package org.dyndns.fzoli.rccar.controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
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
import org.dyndns.fzoli.rccar.controller.resource.R;

/**
 * A járművel kapcsolatos ablakok konténere.
 * @author zoli
 */
public class ControllerWindows {
    
    /**
     * A chatablak ikonja.
     */
    private static final ImageIcon IC_CHAT = R.getImageIcon("chat.png");

    /**
     * A vezérlőablak ikonja.
     */
    private static final ImageIcon IC_ARROWS = R.getImageIcon("arrows.png");

    /**
     * A radarablak ikonja.
     */
    private static final ImageIcon IC_MAP = R.getImageIcon("map.png");
    
    /**
     * A főablak.
     */
    private static class ControllerFrame extends JFrame {

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
        
        public ControllerFrame() {
            super("Főablak");
            setDefaultCloseOperation(HIDE_ON_CLOSE);
            setIconImage(R.getIconImage());
            setLayout(new BorderLayout());
            setResizable(false);
            
            lbImage = new JLabel(IC_BLACK_BG); // amíg nincs MJPEG stream, fekete
            add(lbImage, BorderLayout.CENTER);
            
            JToolBar tbButtons = new JToolBar();
            add(tbButtons, BorderLayout.SOUTH); // az ablak aljára kerülnek a gombok ...
            tbButtons.setFloatable(false); // ... és nem lehet őket onnan elmozdítani
            
            btControll = createButton(tbButtons, null, IC_CONTROLLER1, JButton.class); // vezérlés kérő gomb
            tbButtons.addSeparator(); // szeparátor
            btArrow = createButton(tbButtons, "Vezérlő", IC_ARROWS, JToggleButton.class); // vezérlő ablak láthatóság szabályzó gomb
            btMap = createButton(tbButtons, "Térkép", IC_MAP, JToggleButton.class); // radar ablak láthatóság szabályzó gomb
            btChat = createButton(tbButtons, "Chat", IC_CHAT, JToggleButton.class); // chat ablak láthatóság szabályzó gomb
            tbButtons.addSeparator(); // szeparátor
            btIncrease = createButton(tbButtons, "Növekedő sebesség", IC_INCREASE, JToggleButton.class); // chat ablak láthatóság szabályzó gomb
            
            // kezdetben mindhárom ablak látható, ezért az alapértelmezett érték az, hogy be vannak nyomódva a gombok
            btArrow.setSelected(true);
            btMap.setSelected(true);
            btChat.setSelected(true);
            
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
         * Panelhez gyárt gombot.
         * A panelen lévő gombok nem fókuszálhatóak.
         * @param tb a panel, amihez hozzáadódik a gomb
         * @param text a gomb tooltip szövege
         * @param img a gomb ikonja
         * @param clazz a gomb típusa
         */
        private <T extends AbstractButton> T createButton(JToolBar tb, String text, ImageIcon img, Class<T> clazz) {
            try {
                T bt = clazz.newInstance();
                bt.setIcon(img);
                tb.add(bt);
                bt.setToolTipText(text);
                bt.setFocusable(false);
                return bt;
            }
            catch (Exception ex) {
                return null;
            }
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
