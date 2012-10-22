package org.dyndns.fzoli.rccar.model.device.host;

import java.util.ArrayList;
import java.util.List;
import org.dyndns.fzoli.rccar.model.data.host.HostData;
import org.dyndns.fzoli.rccar.model.device.ChatMessageInfo;
import org.dyndns.fzoli.rccar.model.device.Device;

/**
 *
 * @author zoli
 */
public class HostDevice extends Device<HostData> {
    
    private final List<ChatMessageInfo> CHAT_MESSAGES = new ArrayList<ChatMessageInfo>();
    
    public HostDevice(int deviceId, String commonName, HostData data) {
        super(deviceId, commonName, data);
    }

    public List<ChatMessageInfo> getChatMessages() {
        synchronized (CHAT_MESSAGES) {
            return new ArrayList<ChatMessageInfo>(CHAT_MESSAGES);
        }
    }
    
    public void addChatMessage(ChatMessageInfo m) {
        if (m != null) synchronized(CHAT_MESSAGES) {
            CHAT_MESSAGES.add(m);
        }
    }
    
}
