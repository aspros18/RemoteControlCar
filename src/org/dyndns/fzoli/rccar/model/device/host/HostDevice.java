package org.dyndns.fzoli.rccar.model.device.host;

import java.util.ArrayList;
import java.util.List;
import org.dyndns.fzoli.rccar.model.data.host.HostData;
import org.dyndns.fzoli.rccar.model.device.ChatMessage;
import org.dyndns.fzoli.rccar.model.device.Device;

/**
 *
 * @author zoli
 */
public class HostDevice extends Device<HostData> {
    
    private final List<ChatMessage> CHAT_MESSAGES = new ArrayList<ChatMessage>();
    
    public HostDevice(int deviceId, String commonName, HostData data) {
        super(deviceId, commonName, data);
    }

    public List<ChatMessage> getChatMessages() {
        synchronized (CHAT_MESSAGES) {
            return new ArrayList<ChatMessage>(CHAT_MESSAGES);
        }
    }
    
    public void addChatMessage(ChatMessage m) {
        if (m != null) synchronized(CHAT_MESSAGES) {
            CHAT_MESSAGES.add(m);
        }
    }
    
}
