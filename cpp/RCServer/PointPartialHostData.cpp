/* 
 * File:   PointPartialHostData.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 18., 12:24
 */

#include "PointPartialHostData.h"

REGISTER_DEF_TYPE(PointPartialHostData, org.dyndns.fzoli.rccar.model.host.HostData$PointPartialHostData);

PointPartialHostData::PointPartialHostData() : PartialData<HostData, std::vector<PointData<HostData>>>() {
    ;
}

void PointPartialHostData::add(PointData<HostData> pd) {
    data.push_back(pd);
}

void PointPartialHostData::serialize(Message::Writer& w) {
    w.StartObject();
    w.String("data");
    w.StartArray();
    for (std::vector<PointData<HostData>>::iterator it = data.begin(); it != data.end(); it++) {
        it->serialize(w);
    }
    w.EndArray();
    w.EndObject();
}

void PointPartialHostData::deserialize(Message::Document& d) {
    if (d.IsObject()) {
        if (d.HasMember("data")) {
            Message::Value& v = d["data"];
            if (v.IsArray()) {
                data.clear();
                for (Message::SizeType i = 0; i < v.Size(); i++) {
                    PointData<HostData> pd;
                    pd.deserialize(v[i]);
                    data.push_back(pd);
                }
            }
        }
    }
}

void PointPartialHostData::apply(HostData* d) {
    d->setPointChanging(true);
    for (std::vector<PointData<HostData>>::iterator it = data.begin(); it != data.end(); it++) {
        if (it + 1 == data.end()) d->setPointChanging(false);
        PointData<HostData> pd = *it;
        switch (pd.type) {
            case PointData<HostData>::GPS_POSITION:
                d->setGpsPosition(pd.point);
                break;
            case PointData<HostData>::GRAVITATIONAL_FIELD:
                d->setGravitationalField(pd.point);
                break;
            case PointData<HostData>::MAGNETIC_FIELD:
                d->setMagneticField(pd.point);
        }
    }
}
