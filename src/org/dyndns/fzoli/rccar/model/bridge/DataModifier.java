package org.dyndns.fzoli.rccar.model.bridge;

import org.dyndns.fzoli.rccar.bridge.config.Permissions;

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
public class DataModifier {
    
}
