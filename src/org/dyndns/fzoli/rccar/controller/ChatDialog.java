package org.dyndns.fzoli.rccar.controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
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
            setBorder(BorderFactory.createEtchedBorder());
            setPreferredSize(new Dimension(490, 200));
        }
    };
    
    public ChatDialog(Window owner, final ControllerWindows windows) {
        super(owner, "Chat", windows);
        setIconImage(IC_CHAT.getImage());
        
        add(createSplitPane(JSplitPane.HORIZONTAL_SPLIT, PANEL_MESSAGES, PANEL_CONTROLLERS));
        
        pack();
    }
    
    /**
     * Két komponens mérete állítható SplitPane segítségével.
     */
    private JSplitPane createSplitPane(int orientation, Component c1, Component c2) {
        JSplitPane pane = new JSplitPane(orientation, c1, c2);
        pane.setContinuousLayout(true);
        pane.setDividerSize(DIVIDER_SIZE);
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
