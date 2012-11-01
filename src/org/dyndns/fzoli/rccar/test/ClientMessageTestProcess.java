package org.dyndns.fzoli.rccar.test;

import org.dyndns.fzoli.socket.handler.AbstractSecureClientHandler;
import org.dyndns.fzoli.socket.process.impl.ClientMessageProcess;
import org.dyndns.fzoli.socket.process.impl.MessageProcess;

/**
 * Üzenetküldő tesztelése kliens oldalon, két szálon.
 * @author zoli
 */
public class ClientMessageTestProcess extends ClientMessageProcess {

    public ClientMessageTestProcess(AbstractSecureClientHandler handler) {
        super(handler);
    }

    public static void onStart(final MessageProcess proc) {
        Runnable tester = new Runnable() { // üzenetküldő tesztelése

            @Override
            public void run() {
                while (!proc.getSocket().isClosed()) { // amíg van kapcsolat
                    proc.sendMessage("nesze"); // nesze üzenet küldése
                    try {
                        Thread.sleep(10); // 10 ezredmásodpercenként
                    }
                    catch (InterruptedException ex) {
                        ;
                    }
                }
            }

        };
        new Thread(tester).start(); // 2 külön szál bombázza a másik oldalt, és úgy kell a socket streambe írni,
        new Thread(tester).start(); // hogy a másik oldal értelmes üzenetet kapjon
    }
    
    public static void onMessage(final MessageProcess proc, Object o) {
        // a másik oldal üzenete a konzolra íródik ki (nesze)
        System.out.println(o + " ; " + proc);
    }
    
    public static void onException(final MessageProcess proc, Exception ex) {
        ex.printStackTrace(); // a kivétel konzolra iratása (pl. Socket closed)
    }
    
    @Override
    public void onStart() {
        onStart(this);
    }

    @Override
    public void onMessage(Object o) {
        onMessage(this, o);
    }

    @Override
    public void onException(Exception ex) {
        onException(this, ex);
        super.onException(ex);
    }
    
}
