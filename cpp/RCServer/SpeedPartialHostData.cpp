/* 
 * File:   SpeedPartialHostData.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 18., 16:42
 */

#include "SpeedPartialHostData.h"

REGISTER_DEF_TYPE(SpeedPartialHostData, org.dyndns.fzoli.rccar.model.host.HostData$SpeedPartialHostData);

SpeedPartialHostData::SpeedPartialHostData() : PartialData<HostData, double>() {
    ;
}

SpeedPartialHostData::SpeedPartialHostData(double d)  : PartialData<HostData, double>(d) {
    ;
}

void SpeedPartialHostData::serialize(Message::Writer& w) {
    w.StartObject();
    w.String("data");
    w.Double(data);
    w.EndObject();
}

void SpeedPartialHostData::deserialize(Message::Document& d) {
    if (d.IsObject() && d.HasMember("data")) {
        Message::Value& v = d["data"];
        if (v.IsDouble()) data = v.GetDouble();
    }
}

void SpeedPartialHostData::apply(HostData* d) {
    d->setSpeed(data);
}
