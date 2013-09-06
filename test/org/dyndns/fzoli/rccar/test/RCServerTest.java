package org.dyndns.fzoli.rccar.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import javax.net.ssl.SSLSocket;
import org.dyndns.fzoli.socket.SSLSocketUtil;

/**
 * C++ szerver teszt.
 * @author zoli
 */
public class RCServerTest {
    
    public static void main(String[] args) throws Exception {
        SSLSocket socket = SSLSocketUtil.createClientSocket("localhost", 9443, new File("test-certs-passwd/ca.crt"), new File("test-certs-passwd/controller.crt"), new File("test-certs-passwd/controller.key"), "asdfgh".toCharArray(), null);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println(in.readLine());
    }
    
}
