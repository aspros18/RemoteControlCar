package org.dyndns.fzoli.rccar.ui;

import java.awt.Dialog;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
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
     * A kivételmegjelenítő ablak címsor szövege.
     */
    private static final String title = "Nem várt hiba";

    /**
     * Az kivételmegjelenítő ablak ikonja.
     */
    private static Image icon;
    
    /**
     * Nincs szükség példányosításra, se öröklésre.
     */
    private UncaughtExceptionHandler() {
    }
    
    /**
     * Létrehozza a kivételmegjelenítő ablak megjelenését beállító objektumot.
     */
    private static UncaughtExceptionParameters createParameters() {
        return new UncaughtExceptionParameters(title, "Nem várt hiba keletkezett a program futása alatt.", "Részletek", "Bezárás", "Másolás", "Mindet kijelöl", icon);
    }
    
    /**
     * Beállítja a címsorban megjelenő ikont.
     */
    public static void setIcon(Image icon) {
        UncaughtExceptionHandler.icon = icon;
    }
    
    /**
     * Alkalmazza a program kivételkezelő metódusát.
     * Ha a rendszerikonok támogatva vannak, dialógusablak jeleníti meg a nem kezelt kivételeket,
     * egyébként nem változik az eredeti kivételkezelés.
     */
    public static void apply() {
        if (SystemTrayIcon.isSupported()) {
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
        UncaughtExceptionDialog.showException(t, ex, error ? Dialog.ModalityType.APPLICATION_MODAL : Dialog.ModalityType.MODELESS, createParameters(), new UncaughtExceptionAdapter() {

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
        final boolean error = ex instanceof Error;
        if (!GraphicsEnvironment.isHeadless()) {
            if (error || !SystemTrayIcon.isVisible()) {
                showExceptionDialog(t, ex);
            }
            else {
                SystemTrayIcon.showMessage(title, "További részletekért kattintson ide.", IconType.ERROR, new Runnable() {
                    
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
