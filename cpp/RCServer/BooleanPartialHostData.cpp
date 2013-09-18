/* 
 * File:   BooleanPartialHostData.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 18., 1:33
 */

#include "BooleanPartialHostData.h"

REGISTER_DEF_TYPE(BooleanPartialHostData, org.dyndns.fzoli.rccar.model.host.HostData$BooleanPartialHostData);

BooleanPartialHostData::BooleanPartialHostData() : BooleanPartialBaseData<HostData>() {
    ;
}

BooleanPartialHostData::BooleanPartialHostData(bool dat, BoolType type) : BooleanPartialBaseData<HostData>(dat, type) {
    ;
}

void BooleanPartialHostData::apply(HostData* d) {
    switch (type) {
        case STREAMING:
            d->setStreaming(data);
            break;
        case VEHICLE_CONNECTED:
            d->setVehicleConnected(data);
            break;
        default:
            BooleanPartialBaseData<HostData>::apply(d);
    }
}
