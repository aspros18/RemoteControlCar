package org.dyndns.fzoli.socket.process.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;
import org.dyndns.fzoli.socket.compress.CompressedBlockInputStream;
import org.dyndns.fzoli.socket.compress.CompressedBlockOutputStream;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.AbstractSecureProcess;

/**
 * Üzenetváltásra használandó szál.
 * Philip Isenhour által írt tömörítést használ az üzenetek továbbítására.
 * @author zoli
 */
public abstract class MessageProcess extends AbstractSecureProcess {
    
    // <editor-fold defaultstate="collapsed" desc="Segédosztályok internetről">
    private static abstract class SimpleWorker implements Runnable {

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

        public SimpleWorker() {
        }

        public SimpleWorker(ObjectOutputStream objectOutput) {
            this.objectOutput = objectOutput;
        }

        public void setObjectOutput(ObjectOutputStream objectOutput) {
            this.objectOutput = objectOutput;
        }

        protected abstract void onException(Exception ex);

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

    private static class Token {

        Object outputMsg;

        public Object getOutputMsg() {
            return outputMsg;
        }

        public void setOutputMsg(Object outputMsg) {
            this.outputMsg = outputMsg;
        }

    }
    // </editor-fold>
    
    /**
     * Üzenetküldést intéző szál.
     * Segítségével több szálból lehet biztonságosan üzenetet küldeni egyazon időben.
     * A másik oldal minden üzenetet megkap, de a sorrend nem biztosított.
     */
    private SimpleWorker worker;
    
    /**
     * Megadja, hogy fut-e az üzenetküldő.
     */
    private boolean running = false;
    
    /**
     * Biztonságos üzenetváltásra képes adatfeldolgozó inicializálása.
     * @param handler Biztonságos kapcsolatfeldolgozó, ami létrehozza ezt az adatfeldolgozót.
     * @throws NullPointerException ha handler null
     */
    public MessageProcess(SecureHandler handler) {
        super(handler);
    }

    /**
     * Megadja, hogy fut-e az üzenetküldő.
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Üzenet küldése a másik oldalnak.
     * A metódus megvárja az üzenetküldés befejezését.
     * @param o az üzenet, szerializálható objektum
     */
    public void sendMessage(Serializable o) {
        sendMessage(o, true);
    }
    
    /**
     * Üzenet küldése a másik oldalnak.
     * @param o az üzenet, szerializálható objektum
     * @param wait várja-e meg a metódus a küldés befejezését
     */
    public void sendMessage(Serializable o, boolean wait) {
        if (worker != null) {
            Token t = new Token();
            t.setOutputMsg(o);
            worker.submitToken(t);
            if (wait) synchronized(t) {
                try {
                    t.wait();
                }
                catch (InterruptedException ex) {
                    ;
                }
            }
        }
    }
    
    /**
     * A másik oldal üzenetet küldött.
     * @param o az üzenet
     */
    protected abstract void onMessage(Object o);
    
    /**
     * A feldolgozó mostantól képes üzenetet küldeni.
     */
    protected void onStart() {
        ;
    }
    
    /**
     * A feldolgozó mostantól nem képes üzenetet küldeni.
     */
    protected void onStop() {
        ;
    }
    
    /**
     * Kivétel keletkezett az egyik üzenet elküldésekor / inicializálás közben / megszakadt a kapcsolat.
     */
    protected void onException(Exception ex) {
        throw new RuntimeException(ex);
    }
    
    /**
     * Leállítja az üzenetküldést intéző szálat.
     * Ha már le lett állítva, nem csinál semmit.
     */
    private void stopWorker() {
        if (worker != null) {
            worker.stop();
            worker = null;
        }
    }
    
    /**
     * Inicializálás.
     * - Üzenetküldő szál létrehozása és indítása.
     * - Várakozás üzenetre a másik oldaltól, míg él a kapcsolat.
     */
    @Override
    public void run() {
        try {
            //ObjectOutputStream létrehozása és átadása az üzenetküldést intéző szálnak
            worker = new SimpleWorker(new ObjectOutputStream(new CompressedBlockOutputStream(getSocket().getOutputStream(), 2000))) {

                @Override
                protected void onException(Exception ex) {
                    // ha bármelyik üzenet küldése közben kivétel keletkezik, jelezi
                    MessageProcess.this.onException(ex);
                }
                
            };
            worker.start(); // dolgozó indítása
            running = true; // az üzenetküldő elindult
            onStart(); // jelzés az utód osztályoknak, hogy lehet üzenni
            // ObjectInputStream létrehozása, ...
            final ObjectInputStream in = new ObjectInputStream(new CompressedBlockInputStream(getSocket().getInputStream()));
            while (!getSocket().isClosed()) { // ... és várakozás üzenetre amíg él a kapcsolat
                onMessage(in.readObject()); // megkapott üzenet feldolgozása
            }
        }
        catch (Exception ex) {
            onException(ex); // ha kivétel keletkezett, jelzés
        }
        finally {
            running = false; // az üzenetküldő leáll
            onStop(); // jelzés az utód osztályoknak, hogy nincs több üzenetküldés
            stopWorker(); // ha befejeződött a feldolgozó futása, dolgozó leállítása
        }
    }
    
}
