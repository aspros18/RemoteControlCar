package org.dyndns.fzoli.socket.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import org.dyndns.fzoli.socket.ServerProcesses;
import org.dyndns.fzoli.socket.handler.exception.HandlerException;
import org.dyndns.fzoli.socket.process.Process;

/**
 * Kapcsolatkezelő szerver oldalra.
 * A socket feldolgozása előtt az adatok alapján kiválasztja, melyik feldolgozót kell indítani.
 * @author zoli
 */
public abstract class AbstractServerHandler extends AbstractHandler {

    private Integer deviceId, connectionId;
    
    /**
     * A szerver oldali kapcsolatkezelő konstruktora.
     * @param socket Socket, amin keresztül folyik a kommunikáció.
     */
    public AbstractServerHandler(Socket socket) {
        super(socket);
    }

    /**
     * Azokat az adatfeldolgozókat adja vissza, melyek még dolgoznak.
     */
    @Override
    public List<Process> getProcesses() {
        return ServerProcesses.getProcesses();
    }
    
    /**
     * A kapcsolatazonosító a szerver oldalon addig nem ismert, míg a kliens nem közli.
     * Ha a kapcsolat létrejön, a második bejövő bájt tartalmazza a kapcsolatazonosítót,
     * ameddig ez nem jön át, a kapcsolatazonosító null értékű marad.
     * @return Kapcsolatazonosító, ami segítségével megtudható a kapcsolatteremtés célja.
     */
    @Override
    public Integer getConnectionId() {
        return connectionId;
    }

    /**
     * Az eszközazonosító a szerver oldalon addig nem ismert, míg a kliens nem közli.
     * Ha a kapcsolat létrejön, az első bejövő bájt tartalmazza az eszközazonosítót,
     * ameddig ez nem jön át, az eszközazonosító null értékű marad.
     * @return Eszközazonosító, ami segítségével megtudható a kliens típusa.
     */
    @Override
    public Integer getDeviceId() {
        return deviceId;
    }
    
    /**
     * Beállítja az eszközazonosítót.
     * Amint a metódus lefutott, már biztonságosan elkérhető az adat.
     */
    private void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }
    
    /**
     * Beállítja a kapcsolatazonosítót.
     * Amint a metódus lefutott, már biztonságosan elkérhető az adat.
     */
    private void setConnectionId(int connectionId) {
        this.connectionId = connectionId;
    }
    
    /**
     * Ha kivétel képződik a szálban, fel kell dolgozni.
     * @param ex a kivétel
     * @throws HandlerException ha nem RuntimeException a kivétel
     * @throws RuntimeException ha RuntimeException a kivétel
     */
    protected void onException(Exception ex) {
        if (ex instanceof RuntimeException) throw (RuntimeException) ex;
        throw new HandlerException(ex);
    }

    /**
     * Ha a kiválasztott Process null, fel kell dolgozni.
     */
    protected void onProcessNull() {
        ;
    }
    
    /**
     * Megpróbálja az üzenetet fogadni a klienstől.
     * Ha a kliens oldalon hiba keletkezett, kivételt dob.
     * @throws IOException ha nem sikerült a fogadás
     * @throws RemoteHandlerException ha a kliens oldalon hiba keletkezett
     */
    private void readStatus(InputStream in) throws IOException {
        AbstractHandlerUtil.readStatus(in);
    }
    
    /**
     * Az inicializáló metódust kivételkezelten meghívja és közli a klienssel az eredményt.
     * @throws Exception ha inicializálás közben kivétel történt
     * @throws IOException ha nem sikerült a kimenetre írni
     */
    private void runInit(OutputStream out) throws IOException, Exception {
        AbstractHandlerUtil.runInit(this, out);
    }
    
    /**
     * Ez a metódus fut le a szálban.
     * Az eszköz- és kapcsolatazonosító klienstől való fogadása után eldől, melyik kapcsolatfeldolgozót
     * kell használni a szerver oldalon és a konkrét feldolgozás kezdődik meg.
     * Ha a feldolgozás végetér, az erőforrások felszabadulnak.
     * @throws HandlerException ha bármi hiba történik
     */
    @Override
    public void run() {
        try {
            
            // stream referenciák megszerzése
            InputStream in = getSocket().getInputStream();
            OutputStream out = getSocket().getOutputStream();
            
            // maximum 3 másodperc van a két bájt olvasására és az inicializálásra
            getSocket().setSoTimeout(3000);
            
            // eszközazonosító elkérése a klienstől
            setDeviceId(in.read());
            // kapcsolatazonosító elkérése a klienstől
            setConnectionId(in.read());
            
            // inicializálás és eredményközlés a kliensnek
            runInit(out);
            
            // eredmény fogadása a klienstől és kivételdobás hiba esetén
            readStatus(in);
            
            // időtúllépés eredeti állapota kikapcsolva
            getSocket().setSoTimeout(0);
            
            // adatfeldolgozó kiválasztása
            Process proc = selectProcess();
            
            if (proc != null) {
                // jelzés, hogy kiválasztódott a Process
                fireProcessSelected();

                // adatfeldolgozó hozzáadása a listához
                getProcesses().add(proc);

                // adatfeldolgozó futtatása
                proc.run();

                // adatfeldolgozó eltávolítása a listából
                getProcesses().remove(proc);
            }
            else {
                // ha nem lett kiválasztva Process, jelzés
                onProcessNull();
            }
            
            // kapcsolat bezárása
            in.close();
            out.flush();
            out.close();
        }
        catch (Exception ex) {
            try {
                getSocket().close();
            }
            catch (Exception e) {
                ;
            }
            onException(ex);
        }
    }
    
}
