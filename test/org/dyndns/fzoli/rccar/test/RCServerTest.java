package org.dyndns.fzoli.rccar.test;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import javax.net.ssl.SSLSocket;
import org.dyndns.fzoli.socket.SSLSocketUtil;

/**
 * C++ szerver teszt.
 * @author zoli
 */
public class RCServerTest {
    
    public static void main(String[] args) throws Exception {
        Gson gson = new Gson();
        String json = gson.toJson(new BagOfPrimitives());
        SSLSocket socket = SSLSocketUtil.createClientSocket("localhost", 9443, new File("test-certs-passwd/ca.crt"), new File("test-certs-passwd/controller.crt"), new File("test-certs-passwd/controller.key"), "asdfgh".toCharArray(), null);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        out.write(json);
        out.flush();
        System.out.println(in.readLine());
        out.close();
        in.close();
        socket.close();
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
