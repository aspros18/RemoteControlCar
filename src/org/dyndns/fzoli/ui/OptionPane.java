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
public class OptionPane extends JOptionPane {
    
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
     * Magyar nyelvű gomb opciók.
     */
    private static final String[] OPTS_OK_EXIT = {"OK", "Kilépés"},
                                  OPTS_YES_NO = {"Igen", "Nem"},
                                  OPTS_OK = {"OK"};
    
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
     * @param showOnTaskbar true esetén megjelenik a tálcán
     * @return a beírt jelszó
     */
    public static PasswordData showPasswordInput(String message, String request, Image icon, boolean saveEnabled, boolean showOnTaskbar) {
        return showPasswordInput(message, request, icon, saveEnabled, showOnTaskbar, null, null);
    }
    
    /**
     * Jelszóbekérő dialógust jelenít meg.
     * Ha a grafikus felület elérhető, dialógus ablakban kéri be a jelszót,
     * egyébként megpróbálja konzolról bekérni a jelszót.
     * Ha nincs se konzol, se grafikus felület, a program kilép.
     * Ha a dialógus ablakon a Kilépésre kattintottak, a program kilép.
     * @param message az első szöveg, ami információközlő
     * @param request a második szöveg, ami kéri a jelszót, hogy írják be
     * @param icon a címsorban megjelenő ikon
     * @param saveEnabled engedélyezve legyen-e a jelszó mentése checkbox
     * @param showOnTaskbar true esetén megjelenik a tálcán
     * @param extraText a középső gomb szövege
     * @param extraCallback a középső gombra kattintás eseménykezelője
     * @return a beírt jelszó, vagy null, ha a középső gombra kattintottak
     */
    public static PasswordData showPasswordInput(String message, String request, Image icon, boolean saveEnabled, boolean showOnTaskbar, String extraText, Runnable extraCallback) {
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
            final boolean hasExtra = extraText != null && extraCallback != null;
            final String[] opts = hasExtra ? new String[] {OPTS_OK_EXIT[0], extraText, OPTS_OK_EXIT[1]} : OPTS_OK_EXIT;
            JFrame dummy = createDummyFrame(icon, showOnTaskbar ? "Jelszó" : null);
            final int option = showOptionDialog(dummy, panel, "Jelszó",
                NO_OPTION, QUESTION_MESSAGE,
                null, opts, opts[0]);
            dummy.dispose();
            if (option == 0) {
                return new PasswordData(pass.getPassword(), save.isSelected());
            }
            else {
                if (hasExtra) {
                    if (option == 1) extraCallback.run();
                    else System.exit(0);
                }
                else {
                    System.exit(0);
                }
            }
        }
        return null;
    }
    
    /**
     * Üzenet dialógust jelenít meg és a megadott képet használja címsor ikonnak.
     * @param icon a címsorba kerülő ikon képe
     * @param message az üzenet
     * @param title a címsor szövege
     * @param messageType az üzenet típusa, az üzenet melletti ikont folyásolja be
     */
    public static void showMessageDialog(Image icon, String message, String title, int messageType) {
        showMessageDialog(createDummyFrame(icon), message, title, messageType);
    }
    
    /**
     * Igen/Nem kérdező dialógust jelenít meg és a megadott képet használja címsor ikonnak.
     * @param icon a címsorba kerülő ikon képe
     * @param message az üzenet
     * @param title a címsor szövege
     */
    public static int showYesNoDialog(Image icon, String message, String title) {
        return showOptionDialog(createDummyFrame(icon), message, title, NO_OPTION, QUESTION_MESSAGE, null, OPTS_YES_NO, OPTS_YES_NO[1]);
    }
    
    /**
     * Figyelmeztető dialógust jelenít meg és a megadott képet használja címsor ikonnak.
     * @param icon a címsorba kerülő ikon képe
     * @param message az üzenet
     * @param title a címsor szövege
     */
    public static int showWarningDialog(Image icon, String message, String title) {
        return showOptionDialog(createDummyFrame(icon), message, title, NO_OPTION, WARNING_MESSAGE, null, OPTS_OK, OPTS_OK[0]);
    }
    
    /**
     * Egy kis csel arra, hogy a címsorban a kért ikon jelenhessen meg.
     * Készít egy ablakot, ami soha nem fog megjelenni, de a dialógus örökli a címsorban lévő ikonját.
     * Teljesen fölösleges lenne, ha lehetne címsor ikont megadni a {@code JOptionPane} metódusainak.
     * @param icon a címsorba kerülő ikon képe
     */
    private static JFrame createDummyFrame(Image icon) {
        return createDummyFrame(icon, null);
    }
    
    /**
     * Egy kis csel arra, hogy a címsorban a kért ikon jelenhessen meg.
     * Készít egy ablakot, ami soha nem fog megjelenni, de a dialógus örökli a címsorban lévő ikonját.
     * Teljesen fölösleges lenne, ha lehetne címsor ikont megadni a {@code JOptionPane} metódusainak.
     * @param icon a címsorba kerülő ikon képe
     * @param title az ablak címsora, amit ha megadnak, akkor a tálcán láthatóvá válik
     */
    private static JFrame createDummyFrame(Image icon, String title) {
        JFrame dummy = null;
        if (icon != null) {
            dummy = new JFrame();
            dummy.setIconImage(icon);
        }
        if (title != null) {
            dummy.setSize(0, 0);
            dummy.setUndecorated(true);
            dummy.setLocationRelativeTo(null);
            dummy.setTitle(title);
            dummy.setVisible(true);
        }
        return dummy;
    }
    
}
