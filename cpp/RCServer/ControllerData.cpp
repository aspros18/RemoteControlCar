/* 
 * File:   TestData.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 16., 19:30
 */

#include "ControllerData.h"
#include "BatteryPartialControllerData.h"
#include "BooleanPartialControllerData.h"
#include "ControlPartialControllerData.h"
#include "HostNamePartialControllerData.h"
#include "HostStatePartialControllerData.h"
#include "OfflineChangeablePartialControllerData.h"

REGISTER_DEF_TYPE(ControllerData, org.dyndns.fzoli.rccar.model.controller.ControllerData);

ControllerData::ControllerData() {
    timeout = -1;
    // test values:
    hostName = "host";
    hostUnderTimeout = false;
    connected = true;
    controlling = false;
    hostUnderTimeout = false;
    vehicleConnected = false;
    viewOnly = true;
    wantControl = false;
}

std::string ControllerData::getHostName() {
    return hostName;
}

void ControllerData::setHostName(std::string name) {
    hostName = name;
}

HostState ControllerData::getHostState() {
    return hostState;
}

void ControllerData::setHostState(HostState state) {
    hostState = state;
}

long ControllerData::getTimeout() {
    return timeout;
}

void ControllerData::setTimeout(long l) {
    timeout = l;
}

bool ControllerData::isHostUnderTimeout() {
    return hostUnderTimeout;
}

void ControllerData::setHostUnderTimeout(bool b) {
    hostUnderTimeout = b;
}

bool ControllerData::isVehicleConnected() {
    return vehicleConnected;
}

void ControllerData::setVehicleConnected(bool b) {
    vehicleConnected = b;
}

bool ControllerData::isControlling() {
    return controlling;
}

void ControllerData::setControlling(bool b) {
    controlling = b;
}

bool ControllerData::isWantControl() {
    return wantControl;
}

void ControllerData::setWantControl(bool b) {
    wantControl = b;
}

bool ControllerData::isViewOnly() {
    return viewOnly;
}

void ControllerData::setViewOnly(bool b) {
    viewOnly = b;
}

bool ControllerData::isConnected() {
    return connected;
}

void ControllerData::setConnected(bool b) {
    connected = b;
}

void ControllerData::serialize(Message::Writer& w) {
    w.StartObject();
    w.String("hostName");
    w.String(hostName.c_str(), hostName.length());
    w.String("hostState");
    hostState.serialize(w);
    w.String("hostUnderTimeout");
    w.Bool(hostUnderTimeout);
    w.String("vehicleConnected");
    w.Bool(vehicleConnected);
    w.String("controlling");
    w.Bool(controlling);
    w.String("wantControl");
    w.Bool(wantControl);
    w.String("viewOnly");
    w.Bool(viewOnly);
    w.String("connected");
    w.Bool(connected);
    if (timeout > -1) {
        w.String("timeout");
        w.Int64(timeout);
    }
    w.String("CHAT_MESSAGES");
    w.StartArray();
    w.EndArray();
    w.String("CONTROLLERS");
    w.StartArray();
    w.EndArray();
    BaseData<ControllerData>::serialize(w);
    w.EndObject();
}

void ControllerData::deserialize(Message::Document& d) {
    if (d.IsObject()) {
        BaseData<ControllerData>::deserialize(d);
        if (d.HasMember("hostName")) {
            Message::Value& v = d["hostName"];
            if (v.IsString()) hostName = std::string(v.GetString());
        }
        if (d.HasMember("hostState")) {
            hostState.deserialize(d["hostState"]);
        }
        if (d.HasMember("hostUnderTimeout")) {
            Message::Value& v = d["hostUnderTimeout"];
            if (v.IsBool()) hostUnderTimeout = v.GetBool();
        }
        if (d.HasMember("vehicleConnected")) {
            Message::Value& v = d["vehicleConnected"];
            if (v.IsBool()) vehicleConnected = v.GetBool();
        }
        if (d.HasMember("controlling")) {
            Message::Value& v = d["controlling"];
            if (v.IsBool()) controlling = v.GetBool();
        }
        if (d.HasMember("wantControl")) {
            Message::Value& v = d["wantControl"];
            if (v.IsBool()) wantControl = v.GetBool();
        }
        if (d.HasMember("viewOnly")) {
            Message::Value& v = d["viewOnly"];
            if (v.IsBool()) viewOnly = v.GetBool();
        }
        if (d.HasMember("connected")) {
            Message::Value& v = d["connected"];
            if (v.IsBool()) connected = v.GetBool();
        }
        if (d.HasMember("timeout")) {
            Message::Value& v = d["timeout"];
            if (v.IsNumber()) timeout = v.GetInt64();
        }
        else {
            timeout = -1;
        }
    }
}

void ControllerData::update(Message* msg) {
    BatteryPartialControllerData* bapd = dynamic_cast<BatteryPartialControllerData*>(msg);
    BooleanPartialControllerData* bopd = dynamic_cast<BooleanPartialControllerData*>(msg);
    ControlPartialControllerData* copd = dynamic_cast<ControlPartialControllerData*>(msg);
    HostNamePartialControllerData* hnpd = dynamic_cast<HostNamePartialControllerData*>(msg);
    HostStatePartialControllerData* hspd = dynamic_cast<HostStatePartialControllerData*>(msg);
    OfflineChangeablePartialControllerData* ocpd = dynamic_cast<OfflineChangeablePartialControllerData*>(msg);
    if (bapd) bapd->apply(this);
    if (bopd) bopd->apply(this);
    if (copd) copd->apply(this);
    if (hnpd) hnpd->apply(this);
    if (hspd) hspd->apply(this);
    if (ocpd) ocpd->apply(this);
}

void ControllerData::update(ControllerData* data) {
    setHostName(data->getHostName());
    setHostState(data->getHostState());
    setTimeout(data->getTimeout());
    setHostUnderTimeout(data->isHostUnderTimeout());
    setConnected(data->isConnected());
    setControlling(data->isControlling());
    setVehicleConnected(data->isVehicleConnected());
    setViewOnly(data->isViewOnly());
    setWantControl(data->isWantControl());
    BaseData<ControllerData>::update(data);
}
