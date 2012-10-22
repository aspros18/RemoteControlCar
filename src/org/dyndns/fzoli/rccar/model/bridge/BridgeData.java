package org.dyndns.fzoli.rccar.model.bridge;

import java.util.ArrayList;
import java.util.List;
import org.dyndns.fzoli.rccar.model.controller.ChatMessageInfo;
import org.dyndns.fzoli.rccar.model.host.HostData;

/**
 *
 * @author zoli
 */
public class BridgeData {
    
    private final String hostName;
    private final HostData hostData = new HostData();
    private final List<ChatMessageInfo> CHAT_MESSAGES = new ArrayList<ChatMessageInfo>();

    public BridgeData(String hostName) {
        this.hostName = hostName;
    }
    
}
