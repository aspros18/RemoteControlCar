package org.dyndns.fzoli.rccar.bridge.socket;

import org.dyndns.fzoli.rccar.model.controller.HostList;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.impl.MessageProcess;

/**
 *
 * @author zoli
 */
public class ControllerSideMessageProcess extends MessageProcess {

    public ControllerSideMessageProcess(SecureHandler handler) {
        super(handler);
    }

    @Override
    protected void onStart() {
        HostList l = new HostList();
        l.addHost("teszt");
        sendMessage(l);
        new Thread(new Runnable() {

            @Override
            public void run() {
                int counter = 0;
                while (!getSocket().isClosed()) {
                    counter++;
                    sendMessage(new HostList.PartialHostList("teszt2", counter % 2 == 0 ? HostList.PartialHostList.ChangeType.REMOVE : HostList.PartialHostList.ChangeType.ADD));
                    try {
                        Thread.sleep(5000);
                    }
                    catch (InterruptedException ex) {
                        ;
                    }
                }
            }
            
        }).start();
    }

    @Override
    protected void onMessage(Object o) {
        ;
    }
    
}
