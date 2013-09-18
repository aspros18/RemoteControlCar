/* 
 * File:   HostNamePartialControllerData.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 18., 23:22
 */

#include "HostNamePartialControllerData.h"

REGISTER_DEF_TYPE(HostNamePartialControllerData, org.dyndns.fzoli.rccar.model.controller.ControllerData$HostNamePartialControllerData);

HostNamePartialControllerData::HostNamePartialControllerData() : PartialData<ControllerData, std::string>() {
    ;
}

HostNamePartialControllerData::HostNamePartialControllerData(std::string s) : PartialData<ControllerData, std::string>(s) {
    ;
}

void HostNamePartialControllerData::apply(ControllerData* d) {
    d->setHostName(data);
}

void HostNamePartialControllerData::serialize(Writer& w) {
    w.StartObject();
    w.String("data");
    w.String(data.c_str());
    w.EndObject();
}

void HostNamePartialControllerData::deserialize(Document& d) {
    if (d.IsObject()) {
        if (d.HasMember("data")) {
            Message::Value& v = d["data"];
            if (v.IsString()) data = std::string(v.GetString());
        }
    }
}
