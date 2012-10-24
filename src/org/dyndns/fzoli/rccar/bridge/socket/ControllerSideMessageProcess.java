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
        l.addHost("teszt1");
        l.addHost("teszt2");
        l.addHost("teszt3");
        l.addHost("teszt4");
        l.addHost("teszt5");
        l.addHost("teszt6");
        l.addHost("teszt7");
        l.addHost("teszt8");
        sendMessage(l);
        new Thread(new Runnable() {

            @Override
            public void run() {
                int counter = 0;
                while (!getSocket().isClosed()) {
                    counter++;
                    sendMessage(new HostList.PartialHostList("teszt9", counter % 2 == 0 ? HostList.PartialHostList.ChangeType.REMOVE : HostList.PartialHostList.ChangeType.ADD));
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
