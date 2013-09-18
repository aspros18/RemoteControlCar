/* 
 * File:   BatteryPartialHostData.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 18., 12:32
 */

#include "BatteryPartialHostData.h"

REGISTER_DEF_TYPE(BatteryPartialHostData, org.dyndns.fzoli.rccar.model.host.HostData$BatteryPartialHostData);

BatteryPartialHostData::BatteryPartialHostData() : BatteryPartialBaseData<HostData>() {
    ;
}

BatteryPartialHostData::BatteryPartialHostData(int dat) : BatteryPartialBaseData<HostData>(dat) {
    ;
}
