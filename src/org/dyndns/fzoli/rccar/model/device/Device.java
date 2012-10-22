package org.dyndns.fzoli.rccar.model.device;

import org.dyndns.fzoli.rccar.model.data.Data;

/**
 *
 * @author zoli
 */
public abstract class Device<D extends Data> {
    
    private final int deviceId;
    private final String commonName;
    private final D data;

    protected Device(int deviceId, String commonName, D data) {
        this.deviceId = deviceId;
        this.commonName = commonName;
        this.data = data;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public String getCommonName() {
        return commonName;
    }

    public D getData() {
        return data;
    }
    
}
