package org.dyndns.fzoli.rccar;

/**
 * Kapcsolat- és eszközazonosítók.
 * @author zoli
 */
public interface ConnectionKeys {
    
    /**
     * Az autót vezérlő telefon eszközazonosítója.
     */
    int KEY_DEV_HOST = 0;
    
    /**
     * Az autót irányító számítógép eszközazonosítója.
     */
    int KEY_DEV_CONTROLLER = 1;
    
    /**
     * Az autót vezérlő telefon eszközazonosítója, több programnyelvhez.
     */
    int KEY_DEV_PURE_HOST = 2;
    
    /**
     * Az autót irányító számítógép eszközazonosítója, több programnyelvhez.
     */
    int KEY_DEV_PURE_CONTROLLER = 3;
    
    /**
     * A kapcsolat megszakadását detektáló szál kapcsolatazonosítója.
     */
    int KEY_CONN_DISCONNECT = 0;
    
    /**
     * Üzenetküldő és fogadó szál kapcsolatazonosítója.
     */
    int KEY_CONN_MESSAGE = 1;
    
    /**
     * MJPEG kamerakép streamelő szál kapcsolatazonosítója.
     */
    int KEY_CONN_VIDEO_STREAM = 2;
    
    /**
     * Csak a tesztelés idejére.
     */
    int KEY_CONN_DUMMY = 3;
    
    /**
     * Az első időtúllépés a kapcsolatban, amikoris a jármű felkészül a kapcsolat megszakadására, tehát megáll.
     * Időkorlát: 1 másodperc
     */
    int DC_TIMEOUT1 = 1000;
    
    /**
     * A második időtúllépés, amikoris a kapcsolat megszakadt, olyan hosszú ideje nem jött válasz.
     * A host ekkor az összes kapcsolatot bezárja, és megpróbál újra kapcsolódni.
     * Időkorlát: 10 másodperc
     */
    int DC_TIMEOUT2 = 10000;
    
    /**
     * Két üzenetváltás között eltelt idő, amit mindkét oldalon ki kell várni a pontos eredmény érdekében.
     * Várakozási idő: 250 ezredmásodperc
     */
    int DC_DELAY = 250;
    
}
