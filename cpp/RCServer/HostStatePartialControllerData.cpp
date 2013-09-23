/* 
 * File:   HostStatePartialControllerData.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 23., 17:56
 */

#include "HostStatePartialControllerData.h"

REGISTER_DEF_TYPE(HostStatePartialControllerData, org.dyndns.fzoli.rccar.model.controller.ControllerData$HostStatePartialControllerData);

HostStatePartialControllerData::HostStatePartialControllerData() : PartialData<ControllerData, HostState>() {
    ;
}

HostStatePartialControllerData::HostStatePartialControllerData(HostState d) : PartialData<ControllerData, HostState>(d) {
    ;
}

void HostStatePartialControllerData::apply(ControllerData* d) {
    d->setHostState(data);
}

void HostStatePartialControllerData::serialize(Writer& w) {
    w.StartObject();
    w.String("data");
    data.serialize(w);
    w.EndObject();
}

void HostStatePartialControllerData::deserialize(Document& d) {
    if (d.IsObject()) {
        if (d.HasMember("data")) {
            data.deserialize(d["data"]);
        }
    }
}
