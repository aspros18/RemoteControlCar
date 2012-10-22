package org.dyndns.fzoli.rccar.model.device;

import java.util.Date;

/**
 *
 * @author zoli
 */
public class ChatMessage {
    
    private final String sender, text;
    private final Date date;

    public ChatMessage(String sender, String text) {
        this.sender = sender;
        this.text = text;
        this.date = new Date();
    }

    public String getSender() {
        return sender;
    }

    public String getText() {
        return text;
    }

    public Date getDate() {
        return date;
    }
    
}
