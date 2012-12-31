package org.dyndns.fzoli.rccar.model;

import org.dyndns.fzoli.socket.process.impl.MessageProcess;

/**
 * Részadat-üzenetküldő generálásához interfész.
 * Az üzenetküldő feladata, hogy a {@link Data} osztály
 * setter metódusait felüldefiniálja meghívva az objektum setter metódusát ha kell,
 * plusz létrehozzon egy vagy több részadatot és küldje is el
 * a {@link MessageProcess} objektumok segítségével.
 * @author zoli
 */
public interface DataSender<D extends Data> {
    
    /**
     * Üzenetküldő létrehozása, mely a setter metódusokban elvégzi az adatküldést a távoli gépnek.
     * Ajánlott úgy implementálni a kommunikációt, hogy a kliensek modeljében ne módosuljon setter hatására
     * a helyi adat, csak az üzenetküldés történjen meg, a szerver viszont küldje vissza a részadatot, ha a kliens
     * jogosult a beállítás elvégzéséhez és ekkor módosuljon a helyi adat. Amint a helyi adat módosult, a felület is frissüljön.
     * Természetesen ha nincs szükség hasonló jogkezelésre és a kliens mindig képes az adat módosítására, akkor a fenti implementáció
     * csak sávpazarlásnak minősül. Ez esetben azonnal módosulhat a helyi adat és a szervernek nem kell visszaküldeni a kérést.
     * @param senderName az üzenetküldő tanúsítványának CN neve
     * @param senderDevice az üzenetküldő eszközazonosítója
     */
    public D createSender(String senderName, Integer senderDevice);
    
}
