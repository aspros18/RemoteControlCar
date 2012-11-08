package org.dyndns.fzoli.rccar.controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
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
        private JToggleButton btChat, btRadar, btArrow;
        
        /**
         * Vezérlőgomb ikonja, amikor átadható a vezérlés.
         */
        private static final ImageIcon icController1 = R.getImageIcon("controller1.png");
        
        /**
         * Vezérlőgomb ikonja, amikor kérhető a vezérlés.
         */
        private static final ImageIcon icController2 = R.getImageIcon("controller2.png");
        
        /**
         * A chatablak ikonja.
         */
        private static final ImageIcon icChat = R.getImageIcon("chat.png");
        
        /**
         * A vezérlőablak ikonja.
         */
        private static final ImageIcon icArrows = R.getImageIcon("arrows.png");
        
        /**
         * A radarablak ikonja.
         */
        private static final ImageIcon icMap = R.getImageIcon("map.png");
        
        /**
         * Teljesen fekete képkocka.
         */
        private static final ImageIcon icBlack = new ImageIcon(new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB) {
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
            
            lbImage = new JLabel(icBlack); // amíg nincs MJPEG stream, fekete
            add(lbImage, BorderLayout.CENTER);
            
            JToolBar tbButtons = new JToolBar();
            add(tbButtons, BorderLayout.SOUTH); // az ablak aljára kerülnek a gombok ...
            tbButtons.setFloatable(false); // ... és nem lehet őket onnan elmozdítani
            
            btControll = createButton(tbButtons, "", icController1, JButton.class); // vezérlés kérő gomb
            tbButtons.addSeparator(); // szeparátor
            btArrow = createButton(tbButtons, "Vezérlő", icArrows, JToggleButton.class); // vezérlő ablak láthatóság szabályzó gomb
            btRadar = createButton(tbButtons, "Radar", icMap, JToggleButton.class); // radar ablak láthatóság szabályzó gomb
            btChat = createButton(tbButtons, "Chat", icChat, JToggleButton.class); // chat ablak láthatóság szabályzó gomb
            
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
         * Az alábbi táblázat alapján XNOR művelet dönti el, hogy ktív-e a gomb
         * és az első opció dönti el az ikon típusát:
         *    vezérli? akarja?  esemény
         *    i        i        lemondás aktív
         *    h        i        kérés inaktív
         *    i        h        lemondás inaktív
         *    h        h        kérés aktív
         */
        public void refresh() {
            btControll.setIcon(getData().isControlling() ? icController1 : icController2);
            btControll.setToolTipText(getData().isControlling() ? "Vezérlés átadása" : "Vezérlés kérése");
            btControll.setEnabled(!(getData().isControlling() ^ getData().isWantControl()));
        }
        
    }
    
    /**
     * A dialógusablakok közös őse.
     */
    private static class ControllerDialog extends JDialog {

        public ControllerDialog(Dialog owner, String title) {
            super(owner, title);
            setIconImage(R.getIconImage());
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
