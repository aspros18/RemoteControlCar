/* 
 * File:   BooleanPartialControllerData.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 18., 13:00
 */

#include "BooleanPartialControllerData.h"

REGISTER_DEF_TYPE(BooleanPartialControllerData, org.dyndns.fzoli.rccar.model.controller.ControllerData$BooleanPartialControllerData);

BooleanPartialControllerData::BooleanPartialControllerData() : BooleanPartialBaseData<ControllerData>() {
    ;
}

BooleanPartialControllerData::BooleanPartialControllerData(bool dat, BoolType type) : BooleanPartialBaseData<ControllerData>(dat, type) {
    ;
}

void BooleanPartialControllerData::apply(ControllerData* d) {
    switch (type) {
        case CONNECTED:
            d->setConnected(data);
            break;
        case VEHICLE_CONNECTED:
            d->setVehicleConnected(data);
            break;
        case HOST_UNDER_TIMEOUT:
            d->setHostUnderTimeout(data);
            break;
        case CONTROLLING:
            d->setControlling(data);
            break;
        case WANT_CONTROLL:
            d->setWantControl(data);
            break;
        case VIEW_ONLY:
            d->setViewOnly(data);
            break;
        default:
            BooleanPartialBaseData<ControllerData>::apply(d);
    }
}
