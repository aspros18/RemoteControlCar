/* 
 * File:   OfflineChangeablePartialControllerData.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 23., 22:33
 */

#include "OfflineChangeablePartialControllerData.h"

OfflineChangeableDatas::OfflineChangeableDatas() {
    ;
}

OfflineChangeableDatas::OfflineChangeableDatas(bool fullX, bool fullY, bool vehicleConnected, bool up2date, HostState state) {
    this->fullX = fullX;
    this->fullY = fullY;
    this->vehicleConnected = vehicleConnected;
    this->up2date = up2date;
    this->state = state;
}

void OfflineChangeableDatas::serialize(Message::Writer& w) {
    w.StartObject();
    w.String("fullX");
    w.Bool(fullX);
    w.String("fullY");
    w.Bool(fullY);
    w.String("vehicleConnected");
    w.Bool(vehicleConnected);
    w.String("up2date");
    w.Bool(up2date);
    w.String("state");
    state.serialize(w);
    w.EndObject();
}

void OfflineChangeableDatas::deserialize(Message::Value& v) {
    if (v.IsObject()) {
        if (v.HasMember("fullX")) {
            Message::Value& val = v["fullX"];
            if (val.IsBool()) fullX = val.GetBool();
        }
        if (v.HasMember("fullY")) {
            Message::Value& val = v["fullY"];
            if (val.IsBool()) fullY = val.GetBool();
        }
        if (v.HasMember("vehicleConnected")) {
            Message::Value& val = v["vehicleConnected"];
            if (val.IsBool()) vehicleConnected = val.GetBool();
        }
        if (v.HasMember("up2date")) {
            Message::Value& val = v["up2date"];
            if (val.IsBool()) up2date = val.GetBool();
        }
        if (v.HasMember("state")) {
            state.deserialize(v["up2date"]);
        }
    }
}

REGISTER_DEF_TYPE(OfflineChangeablePartialControllerData, org.dyndns.fzoli.rccar.model.controller.ControllerData$OfflineChangeablePartialControllerData);

OfflineChangeablePartialControllerData::OfflineChangeablePartialControllerData() : PartialData<ControllerData, OfflineChangeableDatas>() {
    ;
}

OfflineChangeablePartialControllerData::OfflineChangeablePartialControllerData(OfflineChangeableDatas d) : PartialData<ControllerData, OfflineChangeableDatas>(d) {
    ;
}
        
void OfflineChangeablePartialControllerData::apply(ControllerData* d) {
    d->setFullX(data.fullX);
    d->setFullY(data.fullY);
    d->setVehicleConnected(data.vehicleConnected);
    d->setUp2Date(data.up2date);
    d->setHostState(data.state);
}

void OfflineChangeablePartialControllerData::serialize(Message::Writer& w) {
    w.StartObject();
    w.String("data");
    data.serialize(w);
    w.EndObject();
}

void OfflineChangeablePartialControllerData::deserialize(Message::Document& d) {
    if (d.IsObject() && d.HasMember("data")) {
        data.deserialize(d["data"]);
    }
}
