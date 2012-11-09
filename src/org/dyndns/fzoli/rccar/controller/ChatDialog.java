package org.dyndns.fzoli.rccar.controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
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
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
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
    private static final int DIVIDER_SIZE = 4;
    
    /**
     * A vezérlők listája.
     */
    private final JList<String> LIST_CONTROLLERS = new JList<String>(new DefaultListModel<String>()) {
        {
            ((DefaultListModel)getModel()).addElement("controller"); // TODO: tesztsor
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
     */
    private final JPanel PANEL_CONTROLLERS = new JPanel() {
        {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(), BorderFactory.createLineBorder(LIST_CONTROLLERS.getBackground(), 4)));
            JLabel lb = new JLabel("Jelenlévők");
            lb.setOpaque(true);
            lb.setBackground(LIST_CONTROLLERS.getBackground());
            lb.setFont(new Font(lb.getFont().getFontName(), Font.BOLD, lb.getFont().getSize()));
            add(lb, BorderLayout.NORTH);
            add(LIST_CONTROLLERS, BorderLayout.CENTER);
            setPreferredSize(new Dimension(150 - DIVIDER_SIZE, 200));
        }
    };
    
    /**
     * Az üzenetkijelző és üzenetküldő panel.
     */
    private final JPanel PANEL_MESSAGES = new JPanel() {
        {
            setBackground(Color.WHITE);
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEtchedBorder());
            final JLabel lb1 = new JLabel("<html>" + createMessageString(new Date(), "controller", "üzenet", false, false) + "</html>");
            lb1.setVerticalAlignment(SwingConstants.TOP);
            lb1.setHorizontalAlignment(SwingConstants.LEFT);
            final JTextArea lb2 = new JTextArea();
            lb2.setLineWrap(true);
            lb2.setBorder(BorderFactory.createLineBorder(getBackground(), 5));
            
            final String SUBMIT = "text-submit";
            InputMap input = lb2.getInputMap();
            KeyStroke enter = KeyStroke.getKeyStroke("ENTER");
            KeyStroke shiftEnter = KeyStroke.getKeyStroke("shift ENTER");
            input.put(shiftEnter, input.get(enter));
            input.put(enter, SUBMIT);

            ActionMap actions = lb2.getActionMap();
            actions.put(SUBMIT, new AbstractAction() {
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!lb2.getText().trim().isEmpty()) {
                        lb1.setText("<html>" + lb1.getText().substring(6, lb1.getText().length() - 7) + createMessageString(new Date(), "controller", lb2.getText().trim(), true, true) + "</html>");
                        lb2.setText("");
                    }
                }
                
            });
            
            JScrollPane pane1 = new JScrollPane(lb1);
            pane1.setViewportBorder(null);
            pane1.setMinimumSize(lb1.getMinimumSize());
            pane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            
            JScrollPane pane2 = new JScrollPane(lb2);
            pane2.setViewportBorder(null);
            pane2.setMinimumSize(lb2.getMinimumSize());
            pane2.setPreferredSize(new Dimension(490, 50));
            pane2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            add(createSplitPane(JSplitPane.VERTICAL_SPLIT, pane1, pane2));
            setPreferredSize(new Dimension(490, 200));
        }
    };
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
    
    private String createMessageString(Date date, String name, String message, boolean newline, boolean dot) {
        return (newline ? "<br>" : "") + "<span style=\"color:gray\">[" + DATE_FORMAT.format(date) + "]&nbsp;</span><span style=\"color:rgb(0,128,255);font-weight:800\">" + (dot ? "..." : (name + ':')) + "&nbsp;</span>" + message;
    }
    
    public ChatDialog(Window owner, final ControllerWindows windows) {
        super(owner, "Chat", windows);
        setIconImage(IC_CHAT.getImage());
        
        add(createSplitPane(JSplitPane.HORIZONTAL_SPLIT, PANEL_MESSAGES, PANEL_CONTROLLERS));
        
        pack();
    }
    
    /**
     * Két komponens mérete állítható SplitPane segítségével.
     */
    private static JSplitPane createSplitPane(int orientation, Component c1, Component c2) {
        JSplitPane pane = new JSplitPane(orientation, c1, c2);
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
        UIUtil.setSystemLookAndFeel();
        JDialog d = new ChatDialog(null, null);
        d.setLocationRelativeTo(d);
        d.setVisible(true);
    }
    
}
