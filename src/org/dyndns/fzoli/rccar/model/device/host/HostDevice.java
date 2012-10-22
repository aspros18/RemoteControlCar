package org.dyndns.fzoli.rccar.model.device.host;

import org.dyndns.fzoli.rccar.model.data.host.HostData;
import org.dyndns.fzoli.rccar.model.device.Device;

/**
 *
 * @author zoli
 */
public class HostDevice extends Device<HostData> {
    
    public HostDevice(int deviceId, String commonName, HostData data) {
        super(deviceId, commonName, data);
    }
    
}
