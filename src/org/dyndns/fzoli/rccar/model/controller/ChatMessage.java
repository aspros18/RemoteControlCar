package org.dyndns.fzoli.rccar.model.controller;

import java.util.Date;
import org.dyndns.fzoli.rccar.model.PartialBaseData;

/**
 *
 * @author zoli
 */
public class ChatMessage extends PartialBaseData<ControllerData, String> {
    
    private final String SENDER;
    private final Date DATE;
    
    public ChatMessage(String data) {
        super(data);
        SENDER = null;
        DATE = null;
    }
    
    public ChatMessage(String sender, String data) {
        super(data);
        SENDER = sender;
        DATE = new Date();
    }

    public String getSender() {
        return SENDER;
    }

    public Date getDate() {
        return DATE;
    }

    @Override
    public void apply(ControllerData d) {
        if (d != null) {
            d.addChatMessage(this);
        }
    }
    
}
