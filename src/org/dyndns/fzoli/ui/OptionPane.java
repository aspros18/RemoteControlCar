package org.dyndns.fzoli.ui;

import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.Console;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

/**
 * Magyar nyelvű dialógusablakokat hoz létre.
 * @author zoli
 */
public class OptionPane {
    
    /**
     * A jelszómegjelenítő dialógus visszatérési értéke.
     */
    public static class PasswordData {
        
        private final char[] password;
        private final boolean save;

        public PasswordData(char[] password, boolean save) {
            this.password = password;
            this.save = save;
        }

        /**
         * A beírt jelszó.
         */
        public char[] getPassword() {
            return password;
        }

        /**
         * Kérnek-e jelszómentést.
         * @return true, ha kérnek jelszómentést (vagyis be lett pipálva a checkbox)
         */
        public boolean isSave() {
            return save;
        }
        
    }
    
    /**
     * Jelszóbekérő dialógust jelenít meg.
     * Ha a grafikus felület elérhető, dialógus ablakban kéri be a jelszót,
     * egyébként megpróbálja konzolról bekérni a jelszót.
     * Ha nincs se konzol, se grafikus felület, a program kilép.
     * Ha a dialógus ablakon nem az OK-ra kattintottak, a program kilép.
     * @param message az első szöveg, ami információközlő
     * @param request a második szöveg, ami kéri a jelszót, hogy írják be
     * @param icon a címsorban megjelenő ikon
     * @param saveEnabled engedélyezve legyen-e a jelszó mentése checkbox
     */
    public static PasswordData showPasswordInput(String message, String request, Image icon, boolean saveEnabled) {
        if (GraphicsEnvironment.isHeadless()) {
            Console console = System.console();
            if (console == null) {
                UIUtil.alert("Hiba", "Bevitel nem lehetséges!", System.err);
                System.exit(1);
            }
            console.printf("%s%n", message);
            return new PasswordData(console.readPassword(request), false);
        }
        else {
            JPanel panel = new JPanel(new GridLayout(4, 1));
            JLabel lbMessage = new JLabel(message);
            JLabel lbRequest = new JLabel(request);
            JPasswordField pass = new JPasswordField(10);
            JCheckBox save = new JCheckBox("Jelszó mentése");
            save.setEnabled(saveEnabled);
            save.setSelected(false);
            panel.add(lbMessage);
            panel.add(lbRequest);
            panel.add(pass);
            panel.add(save);
            String[] options = new String[] {"OK", "Kilépés"};
            int option = JOptionPane.showOptionDialog(createDummyFrame(icon), panel, "Jelszó",
                JOptionPane.NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[1]);
            if (option == 0) {
                return new PasswordData(pass.getPassword(), save.isSelected());
            }
            else {
                System.exit(0);
            }
        }
        return null;
    }
    
    public static void showMessageDialog(Image icon, String message, String title, int messageType) {
        JOptionPane.showMessageDialog(createDummyFrame(icon), message, title, messageType);
    }
    
    private static JFrame createDummyFrame(Image icon) {
        JFrame dummy = null;
        if (icon != null) {
            dummy = new JFrame();
            dummy.setIconImage(icon);
        }
        return dummy;
    }
    
}
