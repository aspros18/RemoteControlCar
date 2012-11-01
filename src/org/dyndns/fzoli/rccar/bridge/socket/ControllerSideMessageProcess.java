package org.dyndns.fzoli.rccar.bridge.socket;

import org.dyndns.fzoli.rccar.model.controller.HostList;
import org.dyndns.fzoli.socket.handler.AbstractSecureServerHandler;
import org.dyndns.fzoli.socket.process.impl.ServerMessageProcess;

/**
 *
 * @author zoli
 */
public class ControllerSideMessageProcess extends ServerMessageProcess {

    public ControllerSideMessageProcess(AbstractSecureServerHandler handler) {
        super(handler);
    }

    @Override
    public void onStart() {
        HostList l = new HostList();
        for (int i = 1; i <= 8; i++) {
            l.getHosts().add("teszt" + i);
        }
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
    public void onMessage(Object o) {
        ;
    }
    
}
