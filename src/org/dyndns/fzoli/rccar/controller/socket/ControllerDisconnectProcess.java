package org.dyndns.fzoli.rccar.controller.socket;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.dyndns.fzoli.rccar.controller.ConnectionProgressFrame.Status;
import static org.dyndns.fzoli.rccar.controller.Main.showConnectionStatus;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.impl.ClientDisconnectProcess;

/**
 * A vezérlő oldalán vizsgálja, hogy él-e még a kapcsolat a klienssel.
 * @author zoli
 */
public class ControllerDisconnectProcess extends ClientDisconnectProcess {

    /**
     * Tesztelés céljából létrehozott változók.
     */
    private Date lastDate;
    private long max = 0, sum = 0, count = 0;
    private final Date startDate = new Date();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat(" [m:s] ");
    
    public ControllerDisconnectProcess(SecureHandler handler) {
        super(handler, 10000, 250); // 10 mp időtúllépés, 250 ms sleep
    }

    @Override
    public void beforeAnswer() throws Exception {
        lastDate = new Date();
    }
    
    @Override
    public void afterAnswer() throws Exception {
        Date now = new Date();
        long ping = now.getTime() - lastDate.getTime();
        max = Math.max(max, ping);
        sum += ping;
        count++;
        System.out.println(dateFormat.format(new Date(now.getTime() - startDate.getTime())) + ping + " ms (max. " + max + " ms; avg. " + (sum / count) + " ms)");
    }

    /**
     * Ha a kapcsolt megszakadt.
     * Az összes aktív kapcsolatfeldolgozót leállítja, mely ugyan ahhoz az eszközhöz tartozik
     * és megjeleníti a kapcsolódás hiba ablakot.
     */
    @Override
    public void onDisconnect(Exception ex) {
        super.onDisconnect(ex);
        showConnectionStatus(Status.DISCONNECTED);
    }
    
}
