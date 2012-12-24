package org.dyndns.fzoli.rccar.controller.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.InputMap;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import org.dyndns.fzoli.rccar.controller.ControllerWindows;
import static org.dyndns.fzoli.rccar.controller.ControllerWindows.IC_CHAT;
import org.dyndns.fzoli.rccar.controller.ControllerWindows.WindowType;
import org.dyndns.fzoli.ui.UIUtil;

/**
 * Chatablak.
 * @author zoli
 */
public class ChatDialog extends AbstractDialog {

    /**
     * Az elválasztóvonalak szélessége.
     */
    private static final int DIVIDER_SIZE = 5, MARGIN = 2;
    
    /**
     * A szövegeket megjelenítő panelek háttérszíne.
     */
    private static final Color COLOR_BG = Color.WHITE;
    
    /**
     * A vezérlők listája.
     */
    private final JList<String> LIST_CONTROLLERS = new JList<String>(new DefaultListModel<String>()) {
        {
            setBackground(COLOR_BG);
            setDragEnabled(true);
            setCellRenderer(new DefaultListCellRenderer() {

                /**
                 * A lista elemei látszólag nem választhatóak ki és nem soha nincs rajtuk fókusz.
                 */
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    return super.getListCellRendererComponent(list, value, index, false, false);
                }
                
            });
        }
    };
    
    /**
     * Az üzenetkijelző és üzenetküldő panel.
     * Az összes szöveget megjelenítő panel háttérszíne azonos.
     * Ehhez egy kis trükkre volt szükség, hogy minden oprendszeren működjön.
     * Windows LAF esetén valamiért a JLabel felett ott maradt egy csík, és a panel
     * háttérszín beállítása se oldotta meg a gondot, ezért a ScrollPane-nek állítottam
     * be a hátteret, de a ScrollBarra is hatott, ezért annak megadtam a ScrollPane hátterét.
     * Így sikeresen eltünt a zavaró csík.
     */
    private final JPanel PANEL_CONTROLLERS = new JPanel() {
        {
            setBackground(COLOR_BG);
            setLayout(new GridLayout());
            setBorder(null);
            
            JPanel panel = new JPanel(new BorderLayout()) {

                @Override
                public Dimension getPreferredSize() {
                    // a kívánt szélesség 0, hogy a scroll pane teljesen összehúzza, ha azt kérik
                    return new Dimension(0, super.getPreferredSize().height);
                }
                
            };
            panel.setBackground(getBackground());
            
            JScrollPane pane = new JScrollPane(panel);
            pane.getVerticalScrollBar().setOpaque(true);
            Color spbg = (Color) UIManager.getDefaults().get("ScrollPane.background");
            if (spbg != null) pane.getVerticalScrollBar().setBackground(new Color(spbg.getRGB()));
            pane.setBorder(null);
            pane.setOpaque(false);
            pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            pane.setViewportBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(), BorderFactory.createLineBorder(getBackground(), 4)));
            add(pane);
            
            JLabel lb = new JLabel("Jelenlévők");
            lb.setFont(new Font(lb.getFont().getFontName(), Font.BOLD, lb.getFont().getSize()));
            
            panel.add(lb, BorderLayout.NORTH);
            panel.add(LIST_CONTROLLERS, BorderLayout.CENTER);
            
            setMinimumSize(new Dimension(lb.getPreferredSize().width + 14 + pane.getVerticalScrollBar().getPreferredSize().width, 0));
            setPreferredSize(new Dimension(150 - DIVIDER_SIZE - 2 * MARGIN, 200 - 2 * MARGIN));
        }
    };
    
    /**
     * Az üzeneteket megjelenítő komponens.
     */
    private JTextPane tpMessages;
    
    /**
     * Formázott dokumentum az üzenetek megjelenítéséhez.
     */
    private StyledDocument doc;
    
    /**
     * A formázott dokumentum stílusainak kulcsai.
     */
    private static final String KEY_DATE = "date",
                                KEY_NAME = "name",
                                KEY_REGULAR = "regualar";
    
    /**
     * Dátumformázó a chatüzenetek elküldésének idejének kijelzésére.
     */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
    
    /**
     * Az üzenetkijelző és üzenetküldő panel.
     */
    private final JPanel PANEL_MESSAGES = new JPanel() {
        {
            setBackground(COLOR_BG);
            setLayout(new BorderLayout());
            
            tpMessages = new JTextPane();
            tpMessages.setBackground(getBackground());
            tpMessages.setFocusable(false);
            tpMessages.setEditable(false);
            
            doc = tpMessages.getStyledDocument();
            Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
            Style regular = doc.addStyle(KEY_REGULAR, def);
            
            Style date = doc.addStyle(KEY_DATE, regular);
            StyleConstants.setForeground(date, Color.GRAY);
            
            Style name = doc.addStyle(KEY_NAME, regular);
            StyleConstants.setBold(name, true);
            StyleConstants.setForeground(name, new Color(0, 128, 255));
            
            final JTextArea tpSender = new JTextArea();
            tpSender.setBackground(getBackground());
            tpSender.setLineWrap(true);
            tpSender.setFont(UIManager.getDefaults().getFont("Label.font"));
            tpSender.setBorder(BorderFactory.createLineBorder(getBackground(), 5));
            
            Dimension minSize = new Dimension(200, 32);
            
            final JScrollPane paneMessages = new JScrollPane(tpMessages);
            paneMessages.setBorder(null);
            paneMessages.setViewportBorder(BorderFactory.createEtchedBorder());
            paneMessages.setMinimumSize(minSize);
            paneMessages.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            
            final JScrollPane paneSender = new JScrollPane(tpSender);
            paneSender.setBorder(null);
            paneSender.setViewportBorder(BorderFactory.createEtchedBorder());
            paneSender.setMinimumSize(minSize);
            paneSender.setPreferredSize(new Dimension(490, 50));
            paneSender.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            
            final String SUBMIT = "text-submit";
            InputMap input = tpSender.getInputMap();
            KeyStroke enter = KeyStroke.getKeyStroke("ENTER");
            KeyStroke shiftEnter = KeyStroke.getKeyStroke("shift ENTER");
            input.put(shiftEnter, input.get(enter));
            input.put(enter, SUBMIT);
            ActionMap actions = tpSender.getActionMap();
            actions.put(SUBMIT, new AbstractAction() {
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!tpSender.getText().trim().isEmpty()) {
                        addMessage(new Date(), "controller", tpSender.getText()); //TODO
                        tpSender.setText("");
                    }
                }
                
            });
            
            add(createSplitPane(JSplitPane.VERTICAL_SPLIT, paneMessages, paneSender));
            
            setPreferredSize(new Dimension(490, 200 - 2 * MARGIN));
        }
    };
    
    /**
     * Az utolsó üzenetküldő neve.
     */
    private String lastSender;
    
    public ChatDialog(ControllerFrame owner, final ControllerWindows windows) {
        super(owner, "Chat", windows);
        setIconImage(IC_CHAT.getImage());
        setMinimumSize(new Dimension(420, 125));
        
        JSplitPane pane = createSplitPane(JSplitPane.HORIZONTAL_SPLIT, PANEL_MESSAGES, PANEL_CONTROLLERS);
        pane.setBorder(BorderFactory.createEmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN));
        add(pane);
        
        pack();
        
        //TESZT:
        addMessage(new Date(), "controller", "üzenet");
        setControllerVisible("controller", true);
        for (int i = 2; i <= 7; i++) {
            setControllerVisible("controller" + i, true);
        }
        setControllerVisible("egy sokkal hosszabb tesztnév, mint az előzőek", true);
    }
    
    /**
     * A jelenlévők listájához ad hozzá vagy abból vesz el.
     * Ugyan azt a nevet csak egyszer adja hozzá a listához.
     * @param name a beállítandó név
     * @param visible true esetén hozzáadás, egyébként elvevés
     */
    public void setControllerVisible(String name, boolean visible) {
        DefaultListModel model = (DefaultListModel) LIST_CONTROLLERS.getModel();
        if (visible && !model.contains(name)) model.addElement(name);
        if (!visible && model.contains(name)) model.removeElement(name);
    }
    
    /**
     * Chatüzenetet jelenít meg és a scrollt beállítja.
     * @param date az üzenet elküldésének ideje
     * @param name az üzenet feladója
     * @param message az üzenet tartalma
     * @param newline új sor jellel kezdődjön-e a kód
     * @param dot legyen-e név helyett három pont
     */
    public void addMessage(Date date, String name, String message) {
        try {
            boolean startNewline = message.indexOf("\n") == 0; // ha új sorral kezdődik az üzenet, egy újsor jel bent marad
            doc.insertString(doc.getLength(), (lastSender != null ? "\n" : "") + '[' + DATE_FORMAT.format(date) + "] ", doc.getStyle("date"));
            doc.insertString(doc.getLength(), (name.equals(lastSender) ? "..." : (name + ':')) + ' ', doc.getStyle("name"));
            doc.insertString(doc.getLength(), (startNewline ? "\n" : "") + message.trim(), doc.getStyle("regular"));
            lastSender = name;
            tpMessages.select(doc.getLength(), doc.getLength());
        }
        catch (Exception ex) {
            ;
        }
    }
    
    /**
     * Két komponens mérete állítható SplitPane segítségével.
     */
    private static JSplitPane createSplitPane(int orientation, Component c1, Component c2) {
        JSplitPane pane = new JSplitPane(orientation, c1, c2);
        ((BasicSplitPaneUI) pane.getUI()).getDivider().setBorder(null);
        pane.setContinuousLayout(true);
        pane.setDividerSize(DIVIDER_SIZE);
        pane.setResizeWeight(1.0);
        pane.setBorder(null);
        return pane;
    }
    
    /**
     * Az ablak típusát adja vissza.
     * Az ablak azonosításához szükséges.
     */
    @Override
    public WindowType getWindowType() {
        return WindowType.CHAT;
    }
    
    /**
     * Teszt.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                UIUtil.setSystemLookAndFeel();
                JDialog d = new ChatDialog(null, null);
                d.setLocationRelativeTo(d);
                d.setVisible(true);
                System.out.println(d.getContentPane().getSize());
            }
            
        });
    }
    
}
