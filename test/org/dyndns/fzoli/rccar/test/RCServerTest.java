package org.dyndns.fzoli.rccar.test;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import javax.net.ssl.SSLSocket;
import org.dyndns.fzoli.socket.SSLSocketUtil;
import org.dyndns.fzoli.socket.handler.AbstractSecureClientHandler;
import org.dyndns.fzoli.socket.handler.exception.HandlerException;
import org.dyndns.fzoli.socket.process.AbstractSecureProcess;
import org.dyndns.fzoli.socket.process.SecureProcess;

/**
 * C++ szerver teszt.
 * @author zoli
 */
public class RCServerTest {
    
    private static final int CONN_ID = 5, TEST_COUNT = 1;
    
    private static final Runnable TEST = new Runnable() {

        @Override
        public void run() {
            try {
                SSLSocket socket = SSLSocketUtil.createClientSocket("localhost", 9443, new File("test-certs-passwd/ca.crt"), new File("test-certs-passwd/controller.crt"), new File("test-certs-passwd/controller.key"), "asdfgh".toCharArray(), null);
                AbstractSecureClientHandler handler = new RCHandler(socket, CONN_ID) {

        //            @Override
        //            protected void init() {
        //                throw new HandlerException("Remote error");
        //            }

                    @Override
                    protected SecureProcess selectProcess() {
                        return new AbstractSecureProcess(this) {

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

                        };
                    }

                };
                handler.run();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };
    
    public static void main(String[] args) throws Exception {
        for (int i = 0; i < TEST_COUNT; i ++) {
            new Thread(TEST).start();
            Thread.sleep(500);
        }
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
