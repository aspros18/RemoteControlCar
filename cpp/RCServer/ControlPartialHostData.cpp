/* 
 * File:   ControlPartialHostData.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 18., 16:38
 */

#include "ControlPartialHostData.h"

REGISTER_DEF_TYPE(ControlPartialHostData, org.dyndns.fzoli.rccar.model.host.HostData$ControlPartialHostData);

ControlPartialHostData::ControlPartialHostData() : ControlPartialBaseData<HostData>() {
    ;
}

ControlPartialHostData::ControlPartialHostData(Control dat) : ControlPartialBaseData<HostData>(dat) {
    ;
}
