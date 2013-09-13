package org.dyndns.fzoli.rccar.test.cpp;

import java.io.Serializable;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.impl.MessageProcess;
import org.dyndns.fzoli.socket.stream.JsonStreamMethod;
import org.dyndns.fzoli.socket.stream.StreamMethod;

/**
 *
 * @author zoli
 */
public class RCMessageProcess extends MessageProcess {

    public RCMessageProcess(SecureHandler handler) {
        super(handler);
    }

    @Override
    protected StreamMethod createStreamMethod(Integer deviceId) {
        return new JsonStreamMethod();
    }

    @Override
    protected void onStart() {
        sendMessage("test");
    }

    @Override
    protected void onMessage(Serializable msg) {
        System.out.println(msg);
    }
    
}
