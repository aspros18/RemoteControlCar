package chrriis.dj.nativeswing.test;

import java.awt.HeadlessException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

/**
 * System Tray test frame.
 * @author Zolt&aacute;n Farkas
 */
class TestFrame extends JFrame {

    private final DefaultListModel<String> LOGS = new DefaultListModel<String>();
    private final JList<String> LS_LOG = new JList<String>(LOGS);
    private final DateFormat DF = new SimpleDateFormat("HH:mm:ss");

    public TestFrame() throws HeadlessException {
        super("System Tray test");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        add(new JScrollPane(LS_LOG));
        setSize(300, 200);
        setLocationRelativeTo(this);
    }

    public void l(String text) {
        text = "[" + DF.format(new Date()) + "] " + text;
        if (isVisible()) {
            LOGS.addElement(text);
            LS_LOG.ensureIndexIsVisible(LOGS.size() - 1);
        }
        System.out.println(text);
    }

}
