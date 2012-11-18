package org.dyndns.fzoli.rccar.controller.view.map;

/**
 * Eseményfigyelő a térkép dialógushoz.
 * @author zoli
 */
public interface MapLoadListener {

    /**
     * A térkép betöltődött.
     */
    public void loadFinished(MapDialog radar);

}
