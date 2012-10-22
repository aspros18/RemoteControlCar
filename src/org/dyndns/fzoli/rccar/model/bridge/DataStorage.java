package org.dyndns.fzoli.rccar.model.bridge;

import java.util.ArrayList;
import java.util.List;
import org.dyndns.fzoli.rccar.model.controller.ChatMessageInfo;
import org.dyndns.fzoli.rccar.model.controller.ControllerData;
import org.dyndns.fzoli.rccar.model.host.HostData;

/**
 *
 * @author zoli
 */
public class DataStorage {
    
    private final String HOST_NAME;
    private final HostData HOST_DATA = new HostData();
    private final List<ChatMessageInfo> CHAT_MESSAGES = new ArrayList<ChatMessageInfo>();

    public DataStorage(String hostName) {
        HOST_NAME = hostName;
    }

    public String getHostName() {
        return HOST_NAME;
    }

    public List<ChatMessageInfo> getChatMessages() {
        synchronized(CHAT_MESSAGES) {
            return new ArrayList<ChatMessageInfo>(CHAT_MESSAGES);
        }
    }
    
    public void addChatMessage(ChatMessageInfo m) {
        if (m != null) synchronized(CHAT_MESSAGES) {
            CHAT_MESSAGES.add(m);
        }
    }
    
    public ControllerData createControllerData() {
        ControllerData d = new ControllerData();
        d.setBatteryLevel(HOST_DATA.getBatteryLevel());
        d.setGpsPosition(HOST_DATA.getGpsPosition());
        return d;
    }
    
}
