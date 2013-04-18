package org.dyndns.fzoli.rccar.controller.view;

import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.swing.JDialog;
import org.dyndns.fzoli.rccar.controller.ControllerWindows;
import org.dyndns.fzoli.rccar.controller.ControllerWindows.WindowType;

/**
 * A dialógusablakok közös őse.
 * @author zoli
 */
public abstract class AbstractDialog extends JDialog implements RelocalizableWindow {

    /**
     * A kiválasztott járműhöz tartozó adatokat megjelenítő ablakok kollekciója.
     */
    private final ControllerWindows WINDOWS;
    
    /**
     * Megadja, hogy a dialógus {@link #dispose()} metódusa meg lett-e hívva.
     */
    protected boolean disposed = false;
    
    /**
     * Konstruktor.
     * Beállítja az ablakbezárás-eseménykezelőt.
     * @param owner a főablak
     * @param title a címsor felirata
     * @param windows az ablakok konténere
     */
    public AbstractDialog(ControllerFrame owner, String title, final ControllerWindows windows) {
        super(owner, title);
        WINDOWS = windows;
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                if (windows != null) windows.setVisible(getWindowType(), false);
            }
            
        });
    }
    
    /**
     * A főablak referenciája.
     */
    public ControllerFrame getControllerFrame() {
        try {
            return (ControllerFrame) getOwner();
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    /**
     * Az ablak típusát adja vissza.
     * Az ablak azonosításához szükséges.
     */
    public abstract WindowType getWindowType();

    /**
     * A kiválasztott járműhöz tartozó adatokat megjelenítő ablakok kollekcióját adja meg.
     */
    public ControllerWindows getControllerWindows() {
        return WINDOWS;
    }

    /**
     * A tulajdonos ablakból kiveszi magát a dialógus, majd meghívja az ős dispose metódusát.
     * Így ha a főablak megjelenik, a disposálódott, nem használt dialógusok már nem jelennek meg.
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        disposed = true;
        if (getOwner() != null) try {
            Method removeOwnedWindowMethod = Window.class.getDeclaredMethod("removeOwnedWindow", WeakReference.class);
            Field weakThisField = Window.class.getDeclaredField("weakThis");
            weakThisField.setAccessible(true);
            Object weakThis = weakThisField.get(this);
            removeOwnedWindowMethod.setAccessible(true);
            removeOwnedWindowMethod.invoke(getOwner(), weakThis);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        super.dispose();
    }
    
}
