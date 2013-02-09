package org.dyndns.fzoli.rccar.controller.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import javax.swing.*;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.plaf.metal.MetalToolTipUI;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import static org.dyndns.fzoli.rccar.controller.ControllerModels.getData;
import org.dyndns.fzoli.rccar.controller.ControllerWindows;
import static org.dyndns.fzoli.rccar.controller.ControllerWindows.IC_CHAT;
import org.dyndns.fzoli.rccar.controller.ControllerWindows.WindowType;
import org.dyndns.fzoli.rccar.model.controller.ChatMessage;
import org.dyndns.fzoli.rccar.model.controller.ControllerState;
import org.dyndns.fzoli.ui.FixedStyledEditorKit;
import org.dyndns.fzoli.ui.ScrollingDocumentListener;
import org.dyndns.fzoli.ui.UIUtil;
import sun.swing.SwingUtilities2;

/**
 * Chatablak.
 * @author zoli
 */
public class ChatDialog extends AbstractDialog {
    
    /**
     * A vezérlőket megjelenítő listának a modelje.
     */
    private static class ControllerStateListModel extends DefaultListModel<ControllerState> {

        /**
         * Megadja, hogy egy vezérlő benne van-e a listában.
         * A keresést név alapján teszi meg.
         */
        @Override
        public boolean contains(Object elem) {
            if (elem instanceof ControllerState) {
                String name = ((ControllerState) elem).getName();
                Enumeration<ControllerState> e = elements();
                while (e.hasMoreElements()) {
                    if (e.nextElement().getName().equals(name)) return true;
                }
                return false;
            }
            return super.contains(elem);
        }
        
    }
    
    /**
     * Az elválasztóvonalak szélessége.
     */
    private static final int DIVIDER_SIZE = 5, MARGIN = 2, TTM = 2;
    
    /**
     * A szövegeket megjelenítő panelek háttérszíne.
     */
    private static final Color COLOR_BG = Color.WHITE;
    
    /**
     * Két vezérlő állapotot hasonlít össze a nevük alapján.
     */
    private static final Comparator<ControllerState> CMP_CNTRLS = new Comparator<ControllerState>() {

        @Override
        public int compare(ControllerState o1, ControllerState o2) {
            return String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName());
        }
        
    };
    
    /**
     * A vezérlők listája.
     */
    private final JList<ControllerState> LIST_CONTROLLERS = new JList<ControllerState>(new ControllerStateListModel()) {
        {
            setBackground(COLOR_BG);
            setDragEnabled(true);
            setCellRenderer(new DefaultListCellRenderer() {

                /**
                 * A lista elemei látszólag nem választhatóak ki és nem soha nincs rajtuk fókusz.
                 * A saját név dőlt betűvel jelenik meg, ha JLabel alapú a lista felsorolása.
                 */
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    Component c = super.getListCellRendererComponent(list, value, index, getModel().getElementAt(index).isControlling(), false);
                    try {
                        JLabel lb = (JLabel) c;
                        if (isSenderName(value.toString())) lb.setFont(new Font(lb.getFont().getName(), Font.ITALIC, lb.getFont().getSize()));
                        return lb;
                    }
                    catch (Exception ex) {
                        return c;
                    }
                }
                
            });
        }
        
        /**
         * Legyárt egy komponenst, ami a megadott sorban lévő komponenssel egyezik méretileg.
         * @param row a sorindex
         */
        private Component createComponent(int row) {
            return getCellRenderer().getListCellRendererComponent(this, getModel().getElementAt(row), row, false, false);
        }
        
        /**
         * Megadja, hogy az adott sorban lévő elemnek szüksége van-e ToolTip szövegre.
         * @param row a sorindex
         * @return true ha nem fér ki és három pontra végződik, egyébként false
         */
        private boolean needToolTip(int row) {
            Component c = createComponent(row);
            return c.getPreferredSize().width > getSize().width;
        }
        
        /**
         * Azok a nevek, melyek nem férnek ki, három pontra végződnek és az egeret rajtuk tartva a teljes szövegük jelenik meg előttük ToolTip-ben.
         * Ez a metódus adja meg a ToolTip szövegét.
         * @see #getToolTipLocation(java.awt.event.MouseEvent)
         */
        @Override
        public String getToolTipText(MouseEvent e) {
            int row = locationToIndex(e.getPoint());
            if (!needToolTip(row)) return null;
            Object o = getModel().getElementAt(row);
            return o.toString();
        }
        
        /**
         * Azok a nevek, melyek nem férnek ki, három pontra végződnek és az egeret rajtuk tartva a teljes szövegük jelenik meg előttük ToolTip-ben.
         * Ez a metódus adja meg a ToolTip helyét.
         * @see #getToolTipText(java.awt.event.MouseEvent)
         */
        @Override
        public Point getToolTipLocation(MouseEvent e) {
            int row = locationToIndex(e.getPoint());
            if (!needToolTip(row)) return null;
            Rectangle r = getCellBounds(row, row);
            return new Point(r.x - TTM, r.y - TTM);
        }
        
        /**
         * Azok a nevek, melyek nem férnek ki, három pontra végződnek és az egeret rajtuk tartva a teljes szövegük jelenik meg előttük ToolTip-ben.
         * Ahhoz, hogy úgy tűnjön, mint ha a felirat kiérne a panelből, ugyan olyan betűtípussal, háttérszínnel és betűszínnel kell kirajzolódnia
         * a ToolTip-nek, mint a JLabel-nek. Ez a metódus létrehoz egy olyan ToolTip objektumot, ami pontosan így néz ki.
         * A ToolTip szövegének margója a {@link #TTM} alapján állítódik be, amit a pozíció generálásakor is figyelembe kell venni:
         * @see #getToolTipLocation(java.awt.event.MouseEvent)
         */
        @Override
        public JToolTip createToolTip() {
            return new JToolTip() {
                {
                    setComponent(LIST_CONTROLLERS);
                    setUI(new MetalToolTipUI() {

                        {
                            setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
                            setBackground(COLOR_BG);
                        }

                        private Font findFontAndSetColors(String text) {
                            int index = 0;
                            DefaultListModel<ControllerState> model = (DefaultListModel) getModel();
                            Enumeration<ControllerState> e = model.elements();
                            while (e.hasMoreElements()) {
                                ControllerState cs = e.nextElement();
                                if (cs.getName().equals(text)) {
                                    Component cmp = createComponent(index);
                                    if (cmp.isOpaque()) setBackground(cmp.getBackground());
                                    foreground = cmp.getForeground();
                                    return cmp.getFont();
                                }
                                index++;
                            }
                            return null;
                        }

                        @Override
                        public void paint(Graphics g, JComponent c) {
                            String text = ((JToolTip) c).getTipText();
                            Insets insets = getInsets();
                            Font font = findFontAndSetColors(text);
                            if (font == null) font = getFont();
                            g.setFont(font);
                            g.setColor(getForeground());
                            // TODO: kicserélni megbízható osztályra
                            SwingUtilities2.drawString(getComponent(), g, text, insets.left, insets.top + SwingUtilities2.getFontMetrics(c, g, font).getAscent());
                        }

                    });
                }

                private Color foreground;

                @Override
                public Color getForeground() {
                    return foreground == null ? UIManager.getDefaults().getColor("Label.foreground") : foreground;
                }

                @Override
                public Font getFont() {
                    return UIManager.getDefaults().getFont("Label.font");
                }

                @Override
                public Insets getInsets() {
                    Insets ins = ((JLabel)createComponent(0)).getInsets();
                    return new Insets(ins.top + TTM, ins.left + TTM, ins.bottom + TTM, ins.right + TTM);
                }
                
            };
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
            lb.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
            
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
                                KEY_MYNAME = "myname",
                                KEY_SYSNAME = "sysname",
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
            tpMessages.setEditorKit(new FixedStyledEditorKit());
            
            doc = tpMessages.getStyledDocument();
            Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
            Style regular = doc.addStyle(KEY_REGULAR, def);
            
            Style date = doc.addStyle(KEY_DATE, regular);
            StyleConstants.setForeground(date, Color.GRAY);
            
            Style name = doc.addStyle(KEY_NAME, regular);
            StyleConstants.setBold(name, true);
            StyleConstants.setForeground(name, new Color(0, 128, 255));
            
            Style myname = doc.addStyle(KEY_MYNAME, name);
            StyleConstants.setForeground(myname, new Color(0, 100, 205));
            
            Style sysname = doc.addStyle(KEY_SYSNAME, name);
            StyleConstants.setForeground(sysname, Color.BLACK);
            
            final JTextArea tpSender = new JTextArea();
            tpSender.setBackground(getBackground());
            tpSender.setLineWrap(true);
            tpSender.setDocument(new PlainDocument() {

                @Override
                public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                    if(str == null || tpSender.getText().length() >= 1000) throw new BadLocationException(str, offs);
                    super.insertString(offs, str, a);
                }
                
            });
            tpSender.setFont(UIManager.getDefaults().getFont("Label.font"));
            tpSender.setBorder(BorderFactory.createLineBorder(getBackground(), 5));
            
            Dimension minSize = new Dimension(200, 32);
            
            final JScrollPane paneMessages = new JScrollPane(tpMessages);
            paneMessages.setBorder(null);
            paneMessages.setViewportBorder(BorderFactory.createEtchedBorder());
            paneMessages.setMinimumSize(minSize);
            paneMessages.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            ScrollingDocumentListener.apply(tpMessages, paneMessages);
            
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
                    ScrollingDocumentListener.scrollToBottom(tpMessages); // scrollozás az üzenetek végére
                    if (!tpSender.getText().trim().isEmpty()) {
                        getData().getSender().getChatMessages().add(new ChatMessage(tpSender.getText())); // üzenet elküldése
                        tpSender.setText(""); // üzenetkülső panel kiürítése
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
    
    /**
     * A saját felhasználónév.
     */
    private static String senderName;
    
    public ChatDialog(ControllerFrame owner, final ControllerWindows windows) {
        super(owner, "Chat", windows);
        getData().setChatDialog(this);
        setIconImage(IC_CHAT.getImage());
        setMinimumSize(new Dimension(420, 125));
        
        JSplitPane pane = createSplitPane(JSplitPane.HORIZONTAL_SPLIT, PANEL_MESSAGES, PANEL_CONTROLLERS);
        pane.setBorder(BorderFactory.createEmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN));
        add(pane);
        
        pack();
    }
    
    /**
     * A kollekcióban lévő chatüzeneteket hozzáadja a felülethez.
     * @param c a chatüzenetek szépen sorrendben
     */
    public void addChatMessages(Collection<? extends ChatMessage> c) {
        for (ChatMessage msg : c) {
            addMessage(msg.getDate(), msg.getSender(), msg.data);
        }
    }
    
    /**
     * Az összes chatüzenetet eltávolítja a felületről.
     */
    public void removeChatMessages() {
        try {
            lastSender = null;
            doc.remove(0, doc.getLength());
        }
        catch (BadLocationException ex) {
            ;
        }
    }
    
    /**
     * A kollekcióban lévő vezérlő neveket hozzáadja a felülethez.
     * @param c a vezérlők nevei szépen sorrendben
     */
    public void addControllers(Collection<? extends ControllerState> c) {
        for (ControllerState s : c) {
            setControllerVisible(s, true, false);
        }
    }
    
    /**
     * Az összes vezérlő nevének eltávolítása a felületről.
     */
    public void removeControllers() {
        ((DefaultListModel) LIST_CONTROLLERS.getModel()).clear();
    }
    
//    /**
//     * A vezérlők listájában megkeresi az adott vezérlő állapotleíróját.
//     * Így megvizsgálható a felületen lévő model és az aktuális model közti eltérés.
//     * @return null, ha a felületen nem található a keresett vezérlő
//     */
//    public ControllerState findController(String name) {
//        Enumeration<ControllerState> e = ((DefaultListModel) LIST_CONTROLLERS.getModel()).elements();
//        while (e.hasMoreElements()) {
//            ControllerState s = e.nextElement();
//            if (s.getName().equals(name)) return s;
//        }
//        return null;
//    }
    
    /**
     * A jelenlévők listájához ad hozzá vagy abból vesz el.
     * Ugyan azt a nevet csak egyszer adja hozzá a listához.
     * A hozzáadáskor ABC sorrendbe rendeződik a lista.
     * @param state a beállítandó név
     * @param visible true esetén hozzáadás, egyébként elvevés
     */
    public void setControllerVisible(ControllerState state, boolean visible, boolean notify) {
        DefaultListModel<ControllerState> model = (DefaultListModel) LIST_CONTROLLERS.getModel();
        if (visible && !model.contains(state)) {
            List<ControllerState> l = new ArrayList<ControllerState>();
            Enumeration<ControllerState> e = model.elements();
            while (e.hasMoreElements()) {
               l.add(e.nextElement());
            }
            l.add(state);
            Collections.sort(l, CMP_CNTRLS);
            model.clear();
            for (ControllerState s : l) {
                model.addElement(s);
            }
        }
        if (!visible && model.contains(state)) {
            model.removeElement(state);
        }
        if (notify) {
            addMessage(new Date(), state.getName(), (visible ? "kapcsolódott a járműhöz" : "lekapcsolódott a járműről") + '.', true);
        }
    }

    /**
     * Megadja, hogy a megadott felhasználónév megegyezik-e a saját felhasználónévvel.
     */
    private static boolean isSenderName(String senderName) {
        if (senderName == null || ChatDialog.senderName == null) return false;
        return ChatDialog.senderName.equals(senderName);
    }
    
    /**
     * Saját felhasználónév beállítása.
     */
    public static void setSenderName(String senderName) {
        ChatDialog.senderName = senderName;
    }
    
    /**
     * Rendszerüzenetben figyelmezteti a felhasználót, hogy új vezérlő irányítja a járművet.
     * @param d a módosulás dátuma
     * @param name az új irányító neve
     */
    public void showNewController(Date d, String name) {
        addMessage(d, name, "vezérli mostantól a járművet.", true);
    }
    
    /**
     * Rendszerüzenetben figyelmezteti a felhasználót, hogy irányítást kért vagy vont vissza egy vezérlő.
     * @param d a módosulás dátuma
     * @param name a vezérlő neve
     * @param undo true esetén visszavonás történt
     */
    public void showAskControl(Date d, String name, boolean undo) {
        addMessage(d, name, (undo ? "mégsem szeretné vezérelni" : "vezérelni szeretné") + " a járművet.", true);
    }
    
    /**
     * Chatüzenetet jelenít meg és a scrollt beállítja.
     * @param date az üzenet elküldésének ideje
     * @param name az üzenet feladója
     * @param message az üzenet tartalma
     */
    public void addMessage(Date date, String name, String message) {
        addMessage(date, name, message, false);
    }
    
    /**
     * Chatüzenetet illetve rendszerüzenetet jelenít meg és a scrollt beállítja.
     * @param date az üzenet elküldésének ideje
     * @param name az üzenet feladója
     * @param message az üzenet tartalma
     * @param sysmsg rendszerüzenet-e az üzenet
     */
    private void addMessage(Date date, String name, String message, boolean sysmsg) {
        try {
            if (date == null || name == null || message == null) return;
            boolean me = message.indexOf("/me ") == 0;
            if (me) message = message.substring(4);
            me |= sysmsg;
            boolean startNewline = message.indexOf("\n") == 0; // ha új sorral kezdődik az üzenet, egy újsor jel bent marad
            doc.insertString(doc.getLength(), (lastSender != null ? "\n" : "") + '[' + DATE_FORMAT.format(date) + "] ", doc.getStyle(KEY_DATE));
            doc.insertString(doc.getLength(), (me ? ("* " + name) : (name.equals(lastSender) ? "..." : (name + ':'))) + ' ', doc.getStyle(sysmsg ? KEY_SYSNAME : isSenderName(name) ? KEY_MYNAME : KEY_NAME));
            doc.insertString(doc.getLength(), (!me && startNewline ? "\n" : "") + message.trim(), doc.getStyle(KEY_REGULAR));
            lastSender = me ? "" : name;
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
        ChatDialog.setSenderName("controller");
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                UIUtil.setSystemLookAndFeel();
                JDialog d = new ChatDialog(null, null);
                d.addWindowListener(new WindowAdapter() {

                    @Override
                    public void windowClosing(WindowEvent e) {
                        System.exit(0);
                    }
                    
                });
                d.setLocationRelativeTo(d);
                d.setVisible(true);
                System.out.println(d.getContentPane().getSize());
            }
            
        });
    }
    
}
