/* 
 * File:   BatteryPartialControllerData.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 17., 20:56
 */

#include "BatteryPartialControllerData.h"

REGISTER_DEF_TYPE(BatteryPartialControllerData, org.dyndns.fzoli.rccar.model.controller.ControllerData$BatteryPartialControllerData);

BatteryPartialControllerData::BatteryPartialControllerData() : BatteryPartialBaseData<ControllerData>() {
    ;
}

BatteryPartialControllerData::BatteryPartialControllerData(int dat) : BatteryPartialBaseData<ControllerData>(dat) {
    ;
}
