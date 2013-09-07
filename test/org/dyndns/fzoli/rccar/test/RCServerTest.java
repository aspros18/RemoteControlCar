package org.dyndns.fzoli.rccar.test;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import javax.net.ssl.SSLSocket;
import org.dyndns.fzoli.socket.SSLSocketUtil;
import org.dyndns.fzoli.socket.handler.AbstractSecureClientHandler;
import org.dyndns.fzoli.socket.process.AbstractSecureProcess;
import org.dyndns.fzoli.socket.process.SecureProcess;

/**
 * C++ szerver teszt.
 * @author zoli
 */
public class RCServerTest {
    
    private static final int DEVICE_ID = 5;
    
    public static void main(String[] args) throws Exception {
        SSLSocket socket = SSLSocketUtil.createClientSocket("localhost", 9443, new File("test-certs-passwd/ca.crt"), new File("test-certs-passwd/controller.crt"), new File("test-certs-passwd/controller.key"), "asdfgh".toCharArray(), null);
        AbstractSecureClientHandler handler = new RCHandler(socket, DEVICE_ID) {
            
            @Override
            protected SecureProcess selectProcess() {
                return new AbstractSecureProcess(this) {
                    
                    @Override
                    public void run() {
                        try {
//                            Gson gson = new Gson();
//                            String json = gson.toJson(new BagOfPrimitives());
//                            PrintWriter out = new PrintWriter(getSocket().getOutputStream());
                            BufferedReader in = new BufferedReader(new InputStreamReader(getSocket().getInputStream()));
//                            out.write(json + "\r\n");
                            System.out.println(in.readLine());
                        }
                        catch (Exception ex) {
                            ;
                        }
                    }
                    
                };
            }
            
        };
        handler.run();
    }
    
}

class BagOfPrimitives {
    
    private int value1 = 1;
    private String value2 = "abc";
    private transient int value3 = 3;
      
    BagOfPrimitives() {
        // no-args constructor
    }
      
}
