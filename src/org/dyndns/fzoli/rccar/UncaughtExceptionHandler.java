package org.dyndns.fzoli.rccar;

import java.awt.Dialog;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.dyndns.fzoli.exceptiondialog.UncaughtExceptionDialog;
import org.dyndns.fzoli.exceptiondialog.UncaughtExceptionParameters;
import org.dyndns.fzoli.exceptiondialog.event.UncaughtExceptionAdapter;

/**
 * A híd nem várt hibáit kezeli le.
 * Error esetén egy modális dialógusablak jelenik meg és a bezárása után kilép a program.
 * Exception esetén a rendszerikonon jelenik meg egy figyelmeztetés, amire kattintva egy nem modális dialógusablak jelenik meg.
 * Ha a grafikus felület nem támogatott, a konzolon jelenik meg a nem kezelt hiba és Error esetén azonnal kilép a program.
 * @author zoli
 */
public class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final String title = "Nem várt hiba";
    private static final UncaughtExceptionParameters params = new UncaughtExceptionParameters(title, "Nem várt hiba keletkezett a program futása alatt.", "Részletek", "Bezárás", "Másolás", "Mindet kijelöl");

    /**
     * Nincs szükség példányosításra, se öröklésre.
     */
    private UncaughtExceptionHandler() {
    }
    
    /**
     * Alkalmazza a híd kivételkezelő metódusát.
     * Ha a rendszerikonok támogatva vannak, dialógusablak jeleníti meg a nem kezelt kivételeket,
     * egyébként nem változik az eredeti kivételkezelés.
     */
    public static void apply() {
        if (SystemTrayIcon.isSupported()) {
            Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
        }
    }
    
    /**
     * Megjeleníti a kivételt egy dialógusablakban.
     * Ha nem kivétel, hanem hiba keletkezett, modálisan jelenik meg az ablak és a bezárása után leáll a program.
     * @param t a szál, amiben a hiba keletkezett
     * @param ex a nem várt hiba
     */
    public static void showExceptionDialog(final Thread t, final Throwable ex) {
        final boolean error = ex instanceof Error;
        UncaughtExceptionDialog.showException(t, ex, error ? Dialog.ModalityType.APPLICATION_MODAL : Dialog.ModalityType.MODELESS, params, new UncaughtExceptionAdapter() {

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
        if (SystemTrayIcon.isVisible()) {
            if (error) {
                showExceptionDialog(t, ex);
            }
            else {
                SystemTrayIcon.showMessage(title, "További részletekért kattintson ide.", TrayIcon.MessageType.ERROR, new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
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
