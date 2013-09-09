package org.dyndns.fzoli.rccar.test.cpp;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.AbstractSecureProcess;

/**
 *
 * @author zoli
 */
public class RCTestProcess extends AbstractSecureProcess {

    public RCTestProcess(SecureHandler handler) {
        super(handler);
    }

    @Override
    public void run() {
        try {
            getSocket().setSoTimeout(1000);
            System.out.println("conn id: " + getConnectionId());
            Gson gson = new Gson();
            String json = gson.toJson(new BagOfPrimitives());
            BufferedReader in = new BufferedReader(new InputStreamReader(getSocket().getInputStream()));
            OutputStream out = getSocket().getOutputStream();
            out.write(json.getBytes());
            System.out.println(in.readLine());
        }
        catch (Exception ex) {
            ;
        }
    }
    
}
