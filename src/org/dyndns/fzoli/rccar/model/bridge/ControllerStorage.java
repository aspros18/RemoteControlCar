package org.dyndns.fzoli.rccar.model.bridge;

/**
 * Egy konkrét vezerlő kliens adatait tartalmazó tároló.
 * @author zoli
 */
public class ControllerStorage implements Storage {
    
    /**
     * A vezérlő neve.
     */
    private final String CONTROLLER_NAME;

    public ControllerStorage(String controllerName) {
        CONTROLLER_NAME = controllerName;
    }

    @Override
    public String getName() {
        return CONTROLLER_NAME;
    }
    
}
