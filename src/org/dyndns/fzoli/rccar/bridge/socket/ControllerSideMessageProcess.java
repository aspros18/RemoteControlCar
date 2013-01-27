package org.dyndns.fzoli.rccar.bridge.socket;

import java.io.Serializable;
import java.util.ArrayList;
import org.dyndns.fzoli.rccar.ConnectionKeys;
import org.dyndns.fzoli.rccar.model.Control;
import org.dyndns.fzoli.rccar.model.ControlPartialBaseData;
import org.dyndns.fzoli.rccar.model.Point3D;
import org.dyndns.fzoli.rccar.model.controller.ChatMessage;
import org.dyndns.fzoli.rccar.model.controller.ControllerData;
import org.dyndns.fzoli.rccar.model.controller.ControllerData.ChatMessagePartialControllerData;
import org.dyndns.fzoli.rccar.model.controller.ControllerState;
import org.dyndns.fzoli.rccar.model.controller.HostList;
import org.dyndns.fzoli.rccar.model.controller.HostState;
import org.dyndns.fzoli.rccar.model.host.HostData;
import org.dyndns.fzoli.rccar.model.host.HostData.ControlPartialHostData;
import org.dyndns.fzoli.socket.ServerProcesses;
import org.dyndns.fzoli.socket.handler.SecureHandler;

/**
 *
 * @author zoli
 */
public class ControllerSideMessageProcess extends BridgeMessageProcess implements ConnectionKeys {
    
    private String selected;
    
    public ControllerSideMessageProcess(SecureHandler handler) {
        super(handler);
    }

    @Override
    protected void onStart() {
        selected = null;
        HostList l = new HostList();
        for (int i = 1; i <= 8; i++) {
            if (i == 5) continue;
            l.getHosts().add("teszt" + i);
        }
        sendMessage(l);
        new Thread(new Runnable() {

            @Override
            public void run() {
                int counter = 0;
                while (selected == null && !getSocket().isClosed()) {
                    counter++;
                    sendMessage(new HostList.PartialHostList("teszt5", counter % 2 == 0 ? HostList.PartialHostList.ChangeType.REMOVE : HostList.PartialHostList.ChangeType.ADD));
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
    protected void onMessage(Serializable o) {
        if (o instanceof ControllerData.HostNamePartialControllerData) {
            ControllerData.HostNamePartialControllerData msg = (ControllerData.HostNamePartialControllerData) o;
            selected = msg.data;
            if (selected != null) {
                
                if (selected.equals("teszt1")) {
                    ControllerSideVideoProcess mjpeg = ServerProcesses.findProcess(getRemoteCommonName(), getDeviceId(), KEY_CONN_VIDEO_STREAM, ControllerSideVideoProcess.class);
                    if (mjpeg != null) mjpeg.resendFrame();
                }
                ArrayList<ControllerState> nams = new ArrayList<ControllerState>();
                nams.add(new ControllerState("controller"));
                ArrayList<ChatMessage> msgs = new ArrayList<ChatMessage>();
                msgs.add(new ChatMessage("controller", "tesztüzenet"));
                ControllerData data = new ControllerData(nams, msgs);
                data.setHostName(selected);
                data.setVehicleConnected(true);
                data.setWantControl(false);
                data.setViewOnly(false);
                data.setBatteryLevel(30);
                data.setControlling(selected.equals("teszt1") || selected.equals("teszt2"));
                data.setHostState(new HostState(selected.equals("teszt2") ? null : new Point3D(0, 0, 0), selected.equals("teszt1") ? 1 : 5, 0));
                data.setHostUnderTimeout(!(selected.equals("teszt1") || selected.equals("teszt2") || selected.equals("teszt3")));
                data.setVehicleConnected(selected.equals("teszt1") || selected.equals("teszt2"));
                data.setControl(selected.equals("teszt1") ? new Control(0, 0) : new Control(100, -100));
                data.setFullX(selected.equals("teszt1"));
                data.setFullY(selected.equals("teszt1"));
                data.setUp2Date(selected.equals("teszt1") || selected.equals("teszt2"));
                sendMessage(data);
            }
            else {
                onStart();
            }
        }
        else if (o instanceof ControllerData.ChatMessagePartialControllerData) {
            ControllerData.ChatMessagePartialControllerData msg = (ControllerData.ChatMessagePartialControllerData) o;
            ChatMessagePartialControllerData gen = new ControllerData.ChatMessagePartialControllerData(new ChatMessage(getRemoteCommonName(), msg.data.data));
            for (ControllerSideMessageProcess proc : ServerProcesses.getProcesses(ControllerSideMessageProcess.class)) {
                proc.sendMessage(gen);
            }
        }
        else if (o instanceof ControlPartialBaseData) {
            ControlPartialBaseData<?> msg = (ControlPartialBaseData<?>) o;
            HostData.ControlPartialHostData gen = new ControlPartialHostData(msg.data);
            for (HostSideMessageProcess proc : ServerProcesses.getProcesses(HostSideMessageProcess.class)) {
                proc.sendMessage(gen);
            }
        }
    }
    
}
