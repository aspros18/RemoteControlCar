/* 
 * File:   HostData.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 18., 1:37
 */

#include "HostData.h"

REGISTER_DEF_TYPE(HostData, org.dyndns.fzoli.rccar.model.host.HostData);

HostData::HostData() {
}

void HostData::serialize(Message::Writer& w) {
    w.StartObject();
    BaseData<HostData>::serialize(w);
    w.EndObject();
}

void HostData::deserialize(Message::Document& d) {
    if (d.IsObject()) {
        BaseData<HostData>::deserialize(d);
    }
}
        
void HostData::update(HostData* data) {
    BaseData<HostData>::update(data);
}
