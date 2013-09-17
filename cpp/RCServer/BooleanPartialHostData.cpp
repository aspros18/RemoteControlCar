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

void BooleanPartialHostData::apply(HostData* data) {
    ;
}
