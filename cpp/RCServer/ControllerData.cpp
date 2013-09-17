/* 
 * File:   TestData.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 16., 19:30
 */

#include "ControllerData.h"

REGISTER_DEF_TYPE(ControllerData, org.dyndns.fzoli.rccar.model.controller.ControllerData);

ControllerData::ControllerData() {
    ;
}

void ControllerData::serialize(Message::Writer& writer) {
    writer.StartObject();
    writer.String("hostName");
    writer.String("host");
    writer.String("hostState");
    writer.StartObject();
    writer.String("AZIMUTH");
    writer.Int(105);
    writer.EndObject();
    writer.String("hostUnderTimeout");
    writer.Bool(false);
    writer.String("vehicleConnected");
    writer.Bool(false);
    writer.String("controlling");
    writer.Bool(false);
    writer.String("wantControl");
    writer.Bool(false);
    writer.String("viewOnly");
    writer.Bool(true);
    writer.String("connected");
    writer.Bool(true);
    writer.String("CHAT_MESSAGES");
    writer.StartArray();
    writer.EndArray();
    writer.String("CONTROLLERS");
    writer.StartArray();
    writer.StartObject();
    writer.String("NAME");
    writer.String("controller");
    writer.String("lastModified");
    writer.String("Sep 15, 2013 10:21:50 AM");
    writer.String("controlling");
    writer.Bool(false);
    writer.String("wantControl");
    writer.Bool(false);
    writer.EndObject();
    writer.EndArray();
    writer.String("fullX");
    writer.Bool(false);
    writer.String("fullY");
    writer.Bool(false);
    writer.String("up2date");
    writer.Bool(false);
    writer.String("control");
    writer.StartObject();
    writer.String("mX");
    writer.Int(0);
    writer.String("mY");
    writer.Int(0);
    writer.EndObject();
    writer.EndObject();
}

void ControllerData::deserialize(Message::Document& d) {
    BaseData<ControllerData>::deserialize(d);
}

void ControllerData::update(ControllerData* data) {
    BaseData<ControllerData>::update(data);
}
