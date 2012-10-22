package org.dyndns.fzoli.rccar.model.device.controller;

import org.dyndns.fzoli.rccar.model.data.controller.ControllerData;
import org.dyndns.fzoli.rccar.model.device.Device;

/**
 *
 * @author zoli
 */
public class ControllerDevice extends Device<ControllerData> {
    
    public ControllerDevice(int deviceId, String commonName, ControllerData data) {
        super(deviceId, commonName, data);
    }
    
}
