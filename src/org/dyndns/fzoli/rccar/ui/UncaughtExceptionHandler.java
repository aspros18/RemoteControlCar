package org.dyndns.fzoli.rccar.ui;

import java.awt.Dialog;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import javax.swing.UIManager;
import static javax.swing.UIManager.getString;
import static org.dyndns.fzoli.ui.UIUtil.init;
import org.dyndns.fzoli.ui.exceptiondialog.UncaughtExceptionDialog;
import org.dyndns.fzoli.ui.exceptiondialog.UncaughtExceptionParameters;
import org.dyndns.fzoli.ui.exceptiondialog.event.UncaughtExceptionAdapter;
import org.dyndns.fzoli.ui.systemtray.SystemTrayIcon;
import org.dyndns.fzoli.ui.systemtray.TrayIcon.IconType;

/**
 * A program nem várt hibáit kezeli le.
 * Error esetén egy modális dialógusablak jelenik meg és a bezárása után kilép a program.
 * Exception esetén a rendszerikonon jelenik meg egy figyelmeztetés, amire kattintva egy nem modális dialógusablak jelenik meg.
 * Ha a grafikus felület nem támogatott, a konzolon jelenik meg a nem kezelt hiba és Error esetén azonnal kilép a program.
 * @author zoli
 */
public class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    /**
     * Kulcs a lokalizált szöveghez.
     */
    public static final String KEY_UNEXPECTED_ERROR = "MobileRC.unexpectedError",
                               KEY_UNEXPECTED_ERROR_MSG = "MobileRC.unexpectedErrorMessage",
                               KEY_DETAILS = "MobileRC.details",
                               KEY_COPY = "MobileRC.copy",
                               KEY_SELECT_ALL = "MobileRC.selectAll",
                               KEY_CLICK_FOR_DETAILS = "MobileRC.clickForDetails",
                               KEY_EXIT = "MobileRC.exit", KEY_CLOSE = "MobileRC.close";
    
    /**
     * Az kivételmegjelenítő ablak ikonja.
     */
    private static Image icon;
    
    /**
     * Az alapértelmezett szövegek beállítása.
     */
    static {
        init(KEY_UNEXPECTED_ERROR, "Unexpected error");
        init(KEY_UNEXPECTED_ERROR_MSG, "Unexpected error had been occurred.");
        init(KEY_CLICK_FOR_DETAILS, "Click here for details.");
        init(KEY_DETAILS, "Details");
        init(KEY_COPY, "Copy");
        init(KEY_SELECT_ALL, "Select all");
        init(KEY_CLOSE, "Close");
        init(KEY_EXIT, "Exit");
    }
    
    /**
     * Nincs szükség példányosításra, se öröklésre.
     */
    private UncaughtExceptionHandler() {
    }
    
    /**
     * Létrehozza a kivételmegjelenítő ablak megjelenését beállító objektumot.
     */
    private static UncaughtExceptionParameters createParameters(boolean error) {
        return new UncaughtExceptionParameters(getString(KEY_UNEXPECTED_ERROR), getString(KEY_UNEXPECTED_ERROR_MSG), getString(KEY_DETAILS), error ? getString(KEY_EXIT) : getString(KEY_CLOSE), getString(KEY_COPY), getString(KEY_SELECT_ALL), icon);
    }
    
    /**
     * Beállítja a címsorban megjelenő ikont.
     */
    public static void setIcon(Image icon) {
        UncaughtExceptionHandler.icon = icon;
    }
    
    /**
     * Alkalmazza a program kivételkezelő metódusát.
     * Ha a GUI támogatva van, dialógusablak jeleníti meg a nem kezelt kivételeket,
     * egyébként nem változik az eredeti kivételkezelés.
     */
    public static void apply() {
        if (!GraphicsEnvironment.isHeadless()) {
            Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
        }
    }
    
    /**
     * Alkalmazza a program kivételkezelő metódusát és beállítja a kivételmegjelenítő címsorának ikonját.
     * Ha a rendszerikonok támogatva vannak, dialógusablak jeleníti meg a nem kezelt kivételeket,
     * egyébként nem változik az eredeti kivételkezelés.
     * @param icon a kivételmegjelenítő címsorának ikonja
     */
    public static void apply(Image icon) {
        setIcon(icon);
        apply();
    }
    
    /**
     * Megjeleníti a kivételt egy dialógusablakban.
     * Ha nem kivétel, hanem hiba keletkezett, modálisan jelenik meg az ablak és a bezárása után leáll a program.
     * @param t a szál, amiben a hiba keletkezett
     * @param ex a nem várt hiba
     */
    public static void showExceptionDialog(final Thread t, final Throwable ex) {
        final boolean error = ex instanceof Error;
        UncaughtExceptionDialog.showException(t, ex, error ? Dialog.ModalityType.APPLICATION_MODAL : Dialog.ModalityType.MODELESS, createParameters(error), new UncaughtExceptionAdapter() {

            @Override
            public void exceptionDialogClosed() {
                if (error) System.exit(1);
            }

        });
    }

    /**
     * Közli a kivételt a felhasználóval.
     * Ha a rendszerikon látható, buborékablak közli a kivétel létrejöttét, amire kattintva megjelenik a részletezett dialógusablak.
     * Ha hiba történt, egyből a dialógusablak jelenik meg.
     * Ha a grafikus felület nem érhető el, konzolra íródik a kivétel és ha nem kivétel, hanem hiba keletkezett, azonnal kilép a program.
     * @param ex a nem várt hiba
     */
    public static void showException(final Throwable ex) {
        showException(Thread.currentThread(), ex);
    }
    
    /**
     * Közli a kivételt a felhasználóval.
     * Ha a rendszerikon látható, buborékablak közli a kivétel létrejöttét, amire kattintva megjelenik a részletezett dialógusablak.
     * Ha hiba történt, egyből a dialógusablak jelenik meg.
     * Ha a grafikus felület nem érhető el, konzolra íródik a kivétel és ha nem kivétel, hanem hiba keletkezett, azonnal kilép a program.
     * @param t a szál, amiben a hiba keletkezett
     * @param ex a nem várt hiba
     */
    public static void showException(final Thread t, final Throwable ex) {
        if (t.getName().startsWith("AWT-EventQueue")) return; // TODO: a chat ablak idéz elő egy kivételt valamiért, más megoldást kell találni
        final boolean error = ex instanceof Error;
        if (!GraphicsEnvironment.isHeadless()) {
            if (error || !SystemTrayIcon.isVisible()) {
                showExceptionDialog(t, ex);
            }
            else {
                SystemTrayIcon.showMessage(UIManager.getString(KEY_UNEXPECTED_ERROR), getString(KEY_CLICK_FOR_DETAILS), IconType.ERROR, new Runnable() {
                    
                    @Override
                    public void run() {
                        showExceptionDialog(t, ex);
                    }
                    
                });
            }
        }
        else {
            ex.printStackTrace();
            if (error) System.exit(1);
        }
    }
    
    /**
     * Ha nem kezelt hiba történik, ez a metódus fut le.
     * Ha a rendszerikon nem látható, akkor a konzolra íródik a kivétel.
     * Throwable lehet kivétel vagy hiba is.
     * @param t a szál, amiben a hiba keletkezett
     * @param ex a nem várt Exception vagy Error
     */
    @Override
    public void uncaughtException(final Thread t, final Throwable ex) {
        showException(t, ex);
    }

}
