package org.dyndns.fzoli.rccar.test;

import java.io.Serializable;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.impl.MessageProcess;

/**
 * Üzenetküldő tesztelése két szálon.
 * @author zoli
 */
public class MessageTestProcess extends MessageProcess {

    public MessageTestProcess(SecureHandler handler) {
        super(handler);
    }
    
    @Override
    protected void onStart() {
        Runnable tester = new Runnable() { // üzenetküldő tesztelése

            @Override
            public void run() {
                while (!getSocket().isClosed()) { // amíg van kapcsolat
                    sendMessage("nesze"); // nesze üzenet küldése
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

    @Override
    protected void onMessage(Serializable o) {
        // a másik oldal üzenete a konzolra íródik ki (nesze)
        System.out.println(o);
    }

    @Override
    protected void onException(Exception ex) {
        ex.printStackTrace(); // a kivétel konzolra iratása (pl. Socket closed)
        super.onException(ex);
    }
    
}
