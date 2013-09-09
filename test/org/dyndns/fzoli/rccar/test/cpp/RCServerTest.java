package org.dyndns.fzoli.rccar.test.cpp;

import java.io.File;
import javax.net.ssl.SSLSocket;
import org.dyndns.fzoli.socket.SSLSocketUtil;

/**
 * C++ szerver teszt.
 * @author zoli
 */
public class RCServerTest {
    
    private static final int CONN_ID = 1, TEST_COUNT = 1;
    
    private static final Runnable TEST = new Runnable() {

        @Override
        public void run() {
            try {
                SSLSocket socket = SSLSocketUtil.createClientSocket("fzoli.dyndns.org", 9443, new File("test-certs-passwd/ca.crt"), new File("test-certs-passwd/controller.crt"), new File("test-certs-passwd/controller.key"), "asdfgh".toCharArray(), null);
                RCHandler handler = new RCHandler(socket, CONN_ID);
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
//            Thread.sleep(1000);
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
