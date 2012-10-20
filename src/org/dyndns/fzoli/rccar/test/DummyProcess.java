package org.dyndns.fzoli.rccar.test;

import java.io.IOException;  
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Vector;
import org.dyndns.fzoli.rccar.controller.SplashScreenLoader;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.AbstractSecureProcess;

class SimpleWorker implements Runnable {
    
    // begin worker thread control
    private Thread runThread = null;
    private boolean running = false;
    
    public synchronized void start() {
        if (runThread != null && runThread.isAlive()) {
            throw new IllegalStateException("worker thread is already running.");
        }
        running = true;
        runThread = new Thread(this);
        runThread.start();
    }
    
    public synchronized void stop() {
        running = false;
        if (runThread != null) {
            runThread.interrupt();
        }
        runThread = null;
    }
    // end worker thread control
    
    // the queue of things to be written
    private Vector<Token> queue = new Vector<Token>();
    
    public void submitToken(Token t) {
        queue.add(t);
    }
    
    // the object output stream.
    // should be set before our thread is started.
    private ObjectOutputStream objectOutput;
    
    public ObjectOutputStream getObjectOutput() {
        return objectOutput;
    }
    
    public void setObjectOutput(ObjectOutputStream objectOutput) {
        this.objectOutput = objectOutput;
    }
    
    protected void onException(Exception ex) {}
    
    @Override
    public void run() {
        while (running) {
            if (queue.size() == 0) {
                try {
                    Thread.sleep(20);
                }
                catch (InterruptedException ex) {
                    // empty
                }
                continue;
            }
            Token aToken = queue.remove(0);
            try {
                objectOutput.writeObject(aToken.outputMsg);
                objectOutput.flush();
            }
            catch (IOException e) {
                onException(e);
            }
            // notify the thread that submitted this token we are done with it.
            synchronized(aToken) {
                aToken.notify();
            }
        } // while
    }
    
}

class Token {
    
    Object outputMsg;
    
    public Object getOutputMsg() {
        return outputMsg;
    }
    
    public void setOutputMsg(Object outputMsg) {
        this.outputMsg = outputMsg;
    }
    
}

/**
 * Azt tesztelem, hogy mi történik, ha olvasás közben bezárom a socketet másik szálból.
 * Következő probléma: több szálból való szerializálás és küldés egy időben
 * Eredmény: java.net.SocketException: Socket closed
 * @author zoli
 */
public class DummyProcess extends AbstractSecureProcess {
    
    public DummyProcess(SecureHandler handler) {
        super(handler);
    }

    private static class Test1 implements Serializable {
        public int x = 1;
    }
    
    private static class Test2 implements Serializable {
        public int y = 2;
    }
    
    private static class SenderTest implements Runnable {

        private final SimpleWorker worker;
        private final boolean test1;
        
        public SenderTest(SimpleWorker worker, boolean test1) {
            this.worker = worker;
            this.test1 = test1;
        }
        
        @Override
        public void run() {
            try {
                while (true) {
                    // then in each of your threads
                    Token t = new Token();
                    t.setOutputMsg(test1 ? new Test1() : new Test2());
                    worker.submitToken(t);
                    // now wait until the worker gets this written out. the worker will notify this object when its done serializing it.
                    synchronized(t) {
                        t.wait();
                    }
                    Thread.sleep(10);
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    @Override
    public void run() {
        try {
            SplashScreenLoader.closeSplashScreen();
            System.out.println("Device id: " + getDeviceId());
            System.out.println("Connection id: " + getConnectionId());
            final OutputStream out = getSocket().getOutputStream();
            final ObjectOutputStream oout = new ObjectOutputStream(out);
            final SimpleWorker worker = new SimpleWorker();
            worker.setObjectOutput(oout);
            worker.start();
            new Thread(new SenderTest(worker, true)).start();
            new Thread(new SenderTest(worker, false)).start();
            InputStream in = getSocket().getInputStream();
            ObjectInputStream oin = new ObjectInputStream(in);
            while (!getSocket().isClosed()) {
                System.out.println(oin.readObject() instanceof Test1);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
}
