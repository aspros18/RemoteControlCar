/* 
 * File:   ControlPartialControllerData.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 17., 22:18
 */

#include "ControlPartialControllerData.h"

REGISTER_DEF_TYPE(ControlPartialControllerData, org.dyndns.fzoli.rccar.model.controller.ControllerData$ControlPartialControllerData);

ControlPartialControllerData::ControlPartialControllerData() : ControlPartialBaseData<ControllerData>() {
    ;
}

ControlPartialControllerData::ControlPartialControllerData(Control dat) : ControlPartialBaseData<ControllerData>(dat) {
    ;
}
