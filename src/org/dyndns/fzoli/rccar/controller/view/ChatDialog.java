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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import static org.dyndns.fzoli.rccar.controller.Main.getString;
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
     * A "Jelenlévők" szöveget tartalmazó címke.
     */
    private final JLabel LB_CONTROLLERS = new JLabel(getString("controllers"));
    
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
                            int x = insets.left;
                            int y = insets.top + SwingUtilities2.getFontMetrics(c, g, font).getAscent();
                            try {
                                // TODO: kicserélni megbízható osztályra
                                // a JLabel kódja alapján rajzolja ki a tool tip feliratát
                                SwingUtilities2.drawString(getComponent(), g, text, x, y);
                            }
                            catch (Throwable t) {
                                // ha nem sikerült kirajzolni (pl. a SwingUtilities2 osztályt eltávolították)
                                // menti a menthetőt és "rondán" kirajzolja
                                g.drawString(text, x, y);
                            }
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
            
            LB_CONTROLLERS.setFont(new Font(LB_CONTROLLERS.getFont().getFontName(), Font.BOLD, LB_CONTROLLERS.getFont().getSize()));
            LB_CONTROLLERS.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
            
            panel.add(LB_CONTROLLERS, BorderLayout.NORTH);
            panel.add(LIST_CONTROLLERS, BorderLayout.CENTER);
            
            // a minimum size beállítása nem a legszerencsésebb, mivel most már többnyelvű a program és jobb, ha a hosszú "Vezérlők" szöveg vége kipontozódik
            // setMinimumSize(new Dimension(LB_CONTROLLERS.getPreferredSize().width + 14 + pane.getVerticalScrollBar().getPreferredSize().width, 0));
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
    
    /**
     * A használt rendszerüzenetek típusai és szövegük.
     */
    private Map<Integer, String> sysMessages = Collections.synchronizedMap(new HashMap<Integer, String>());
    
    /**
     * A rendszerüzenetek dátumait tartalmazó lista.
     */
    private List<Date> sysDates = Collections.synchronizedList(new ArrayList<Date>());
    
    /**
     * Rendszerüzenet típus.
     */
    private static final int SYS_CONNECT = -1, SYS_DISCONNECT = -2, SYS_CTRL = 0, SYS_WANT_CTRL = 1, SYS_UNDO_CTRL = 2;
    
    /**
     * Konstruktor.
     */
    public ChatDialog(ControllerFrame owner, final ControllerWindows windows) {
        super(owner, getString("chat"), windows);
        getData().setChatDialog(this);
        setIconImage(IC_CHAT.getImage());
        setMinimumSize(new Dimension(420, 125));
        
        JSplitPane pane = createSplitPane(JSplitPane.HORIZONTAL_SPLIT, PANEL_MESSAGES, PANEL_CONTROLLERS);
        pane.setBorder(BorderFactory.createEmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN));
        add(pane);
        
        pack();
    }
    
    /**
     * Kicseréli a chatablak szövegeinek egy részét.
     * @param from a szöveg, amit cserélni kell
     * @param to a szöveg, amire cserélni kell
     * @param start a csere kezdőpozíciója
     * @param sys true esetén csak rendszerüzenetet cserél
     */
    private void replace(String from, String to, int start, boolean sys) {
        try {
            String s = doc.getText(0, doc.getLength()); // az üzenetek teljes szövege
            int i = s.indexOf(from, start); // az első előfordulás a cserélendő szöveghez
            if (i != -1) { // ha van előfordulás
                if (sys) { // ha rendszerüzenetet kell csak cserélni
                    int lineStart = 0; // első sor esetén 0 a sor eleje
                    for (int j = i; j > 0; j--) { // megkeresi a sor elejét
                        if (s.charAt(j) == '\n') {
                            lineStart = j + 1; // a sor eleje megtalálva
                            break;
                        }
                    }
                    int starIndex = lineStart + 11; // a csillag karakter pozíciója
                    boolean isSys; // megadja, hogy rendszerüzenet-e
                    try {
                        isSys = s.charAt(starIndex) == '*'; // elsőként meg kell nézni, csillag karakter van-e a megfelelő helyen
                        if (isSys) { // ha csillag karakter van
                            String chk = s.substring(lineStart, starIndex); // a csillag karakter előtti dátum részletnek ...
                            boolean date = false;
                            for (Date d : sysDates) { // ... illeszkednie kell a rendszerüzenetek dátumait tartalmazó listához
                                if (chk.contains(DATE_FORMAT.format(d))) {
                                    date = true;
                                    break;
                                }
                            }
                            isSys &= date; // most már lehet tudni, hogy rendszerüzenet-e az adott üzenet
                        }
                    }
                    catch (Exception ex) {
                        isSys = false; // hiba esetén biztos, hogy nem rendszerüzenet
                    }
                    if (!isSys) { // ha a találat nem rendszerüzenet ...
                        int lineEnd = s.indexOf("\n", lineStart);
                        if (lineEnd != -1) replace(from, to, lineEnd, sys); // ugrás a következő sorra, ha van és rekurzív hívás
                        return; // a rekurzív hívás után visszatér a kód, ezért itt ki kell lépni
                    }
                }
                // a megtalált sor törölhető, ezért törlés, majd új szöveg beillesztése
                doc.remove(i, from.length());
                doc.insertString(i, to, doc.getStyle(KEY_REGULAR));
                // rekurzív újrahívás, hátha van még cserélni való szöveg
                replace(from, to, ++i, sys);
            }
        }
        catch (Exception ex) {
            ;
        }
    }
    
    /**
     * A felület feliratait újra beállítja.
     * Ha a nyelvet megváltoztatja a felhasználó, ez a metódus hívódik meg.
     */
    @Override
    public void relocalize() {
        setTitle(getString("chat"));
        LB_CONTROLLERS.setText(getString("controllers"));
        Iterator<Entry<Integer, String>> it = sysMessages.entrySet().iterator();
        Map<Integer, String> newValues = new HashMap<Integer, String>();
        while (it.hasNext()) { // a használt rendszerüzenetek lecserélése az új nyelv alapján
            Entry<Integer, String> e = it.next();
            String newValue = getSysText(e.getKey(), "");
            replace(e.getValue(), newValue, 0, true);
            newValues.put(e.getKey(), newValue);
        }
        sysMessages.clear(); // az új értékek nyílvántartásba vétele
        sysMessages.putAll(newValues);
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
            sysMessages.clear();
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
        LIST_CONTROLLERS.invalidate();
        if (notify) {
            showSysMessage(state.getLastModified(), state.getName(), visible ? SYS_CONNECT : SYS_DISCONNECT);
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
        showSysMessage(d, name, SYS_CTRL);
    }
    
    /**
     * Rendszerüzenetben figyelmezteti a felhasználót, hogy irányítást kért vagy vont vissza egy vezérlő.
     * @param d a módosulás dátuma
     * @param name a vezérlő neve
     * @param undo true esetén visszavonás történt
     */
    public void showAskControl(Date d, String name, boolean undo) {
        showSysMessage(d, name, undo ? SYS_UNDO_CTRL : SYS_WANT_CTRL);
    }
    
    /**
     * Megjeleníti a kért rendszerüzenetet.
     * @param d az esemény ideje
     * @param name a módosult felhasználó neve
     * @param type az esemény típusa
     */
    private void showSysMessage(Date d, String name, int type) {
        String s = getSysText(type, null);
        if (s != null) {
            addMessage(d, name, s, true);
            sysMessages.put(type, s);
            sysDates.add(d);
        }
    }
    
    /**
     * Az aktuális nyelv alapján adja meg a kért rendszerüzenet szövegét.
     * @param type a rendszerüzenet típusa
     * @param def ha nincs ilyen típus, ezzel tér vissza
     */
    private String getSysText(int type, String def) {
        String s;
        switch (type) {
            case SYS_CONNECT:
                s = getString("sys_connect");
                break;
            case SYS_DISCONNECT:
                s = getString("sys_disconnect");
                break;
            case SYS_CTRL:
                s = getString("sys_control");
                break;
            case SYS_WANT_CTRL:
                s = getString("sys_want_control");
                break;
            case SYS_UNDO_CTRL:
                s = getString("sys_undo_control");
                break;
            default:
                s = def;
        }
        return s;
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
            doc.insertString(doc.getLength(), (doc.getLength() > 0 ? "\n" : "") + '[' + DATE_FORMAT.format(date) + "] ", doc.getStyle(KEY_DATE));
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
