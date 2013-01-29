package org.dyndns.fzoli.rccar.model.bridge;

import org.dyndns.fzoli.rccar.bridge.config.Permissions;
import org.dyndns.fzoli.rccar.model.Control;
import org.dyndns.fzoli.rccar.model.Point3D;

/**
 * Jogkezelt adatmódosító.
 * Egy konkrét jármű adatait manipulálja.
 * Az adatok módosulásáról részüzenetet is küld mind vezérlő, mind jármű oldalra.
 * Ahhoz, hogy tudni lehessen, kinek kell üzenni és kinek nem, valamint a kérésre
 * van-e jogosultság, meg kell adni a módosítást kezdeményező program
 * azonosító nevét és eszközazonosítóját. Ha ezek nincsenek megadva, nem lesz
 * jogosultság ellenőrzés és a részüzenetek a lehető legtöbb helyre lesznek elküldve.
 * @see ControllerDataForwarder
 * @see HostDataForwarder
 * @see Permissions#onRefresh
 * @author zoli
 */
public abstract class DataModifier {

    /**
     * A módosítást kérő tároló.
     */
    private final Storage MODIFIER;
    
    /**
     * Konstruktor.
     * @param modifier a módosítást kérő tároló
     */
    DataModifier(Storage modifier) {
        MODIFIER = modifier;
    }
    
    /**
     * A jármű tárolóját adja vissza.
     * Ha a módosítást a jármű kéri, akkor
     * a módosítást kérő tároló és a jármű tároló egyazon objektumra mutat.
     * @return null ha nincs a tárolóhoz jármű társítva
     */
    protected abstract HostStorage getHostStorage();
    
    public void setControl(Control control) {}
    
    public void setHostName(String hostName) {}
    
    public void setWantControl(Boolean wantControl) {}
    
    public void setVehicleConnected(Boolean vehicleConnected) {}
    
    public void setUp2Date(Boolean up2date) {}
    
    public void setGpsPosition(Point3D gpsPosition) {}
    
    public void setGravitationalField(Point3D gravitationalField) {}
    
    public void setMagneticField(Point3D magneticField) {}
    
    public void setBatteryLevel(Integer batteryLevel) {}
    
}
