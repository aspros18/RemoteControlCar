/* 
 * File:   HostData.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 18., 1:37
 */

#include "HostData.h"
#include "BatteryPartialHostData.h"
#include "BooleanPartialHostData.h"
#include "ControlPartialHostData.h"
#include "PointPartialHostData.h"
#include "SpeedPartialHostData.h"

REGISTER_DEF_TYPE(HostData, org.dyndns.fzoli.rccar.model.host.HostData);

HostData::HostData() {
    streaming = false;
    vehicleConnected = false;
    pointChanging = false;
    additionalDegree = -1;
    speed = -1;
}

void HostData::serialize(Message::Writer& w) {
    w.StartObject();
    w.String("streaming");
    w.Bool(streaming);
    w.String("vehicleConnected");
    w.Bool(vehicleConnected);
    w.String("pointChanging");
    w.Bool(pointChanging);
    if (speed > -1) {
        w.String("speed");
        w.Double(speed);
    }
    if (additionalDegree > -1) {
        w.String("additionalDegree");
        w.Int(additionalDegree);
    }
    if (gpsPosition.isExists()) {
        w.String("gpsPosition");
        gpsPosition.serialize(w);
    }
    if (gravitationalField.isExists()) {
        w.String("gravitationalField");
        gravitationalField.serialize(w);
    }
    if (magneticField.isExists()) {
        w.String("magneticField");
        magneticField.serialize(w);
    }
    if (previousGpsPosition.isExists()) {
        w.String("previousGpsPosition");
        previousGpsPosition.serialize(w);
    }
    if (previousGravitationalField.isExists()) {
        w.String("previousGravitationalField");
        previousGravitationalField.serialize(w);
    }
    if (previousMagneticField.isExists()) {
        w.String("previousMagneticField");
        previousMagneticField.serialize(w);
    }
    BaseData<HostData>::serialize(w);
    w.EndObject();
}

void HostData::deserialize(Message::Document& d) {
    if (d.IsObject()) {
        if (d.HasMember("streaming")) {
            Message::Value& v = d["streaming"];
            if (v.IsBool()) streaming = v.GetBool();
        }
        if (d.HasMember("vehicleConnected")) {
            Message::Value& v = d["vehicleConnected"];
            if (v.IsBool()) vehicleConnected = v.GetBool();
        }
        if (d.HasMember("pointChanging")) {
            Message::Value& v = d["pointChanging"];
            if (v.IsBool()) pointChanging = v.GetBool();
        }
        if (d.HasMember("additionalDegree")) {
            Message::Value& v = d["additionalDegree"];
            if (v.IsInt()) additionalDegree = v.GetInt64();
        }
        if (d.HasMember("speed")) {
            Message::Value& v = d["speed"];
            if (v.IsDouble()) speed = v.GetDouble();
        }
        if (d.HasMember("gpsPosition")) {
            gpsPosition.deserialize(d["gpsPosition"]);
        }
        else {
            gpsPosition.setExists(false);
        }
        if (d.HasMember("gravitationalField")) {
            gravitationalField.deserialize(d["gravitationalField"]);
        }
        else {
            gravitationalField.setExists(false);
        }
        if (d.HasMember("magneticField")) {
            magneticField.deserialize(d["magneticField"]);
        }
        else {
            magneticField.setExists(false);
        }
        if (d.HasMember("previousGpsPosition")) {
            previousGpsPosition.deserialize(d["previousGpsPosition"]);
        }
        else {
            previousGpsPosition.setExists(false);
        }
        if (d.HasMember("previousGravitationalField")) {
            previousGravitationalField.deserialize(d["previousGravitationalField"]);
        }
        else {
            previousGravitationalField.setExists(false);
        }
        if (d.HasMember("previousMagneticField")) {
            previousMagneticField.deserialize(d["previousMagneticField"]);
        }
        else {
            previousMagneticField.setExists(false);
        }
        BaseData<HostData>::deserialize(d);
    }
}

double HostData::getSpeed() {
    return speed;
}
        
int HostData::getAdditionalDegree() {
    return additionalDegree;
}

bool HostData::isStreaming() {
    return streaming;
}

bool HostData::isVehicleConnected() {
    return vehicleConnected;
}

bool HostData::isPointChanging() {
    return pointChanging;
}

Point3D HostData::getGpsPosition() {
    return gpsPosition;
}

Point3D HostData::getGravitationalField() {
    return gravitationalField;
}

Point3D HostData::getMagneticField() {
    return magneticField;
}

Point3D HostData::getPreviousGpsPosition() {
    return previousGpsPosition;
}

Point3D HostData::getPreviousGravitationalField() {
    return previousGravitationalField;
}

Point3D HostData::getPreviousMagneticField() {
    return previousMagneticField;
}

void HostData::setSpeed(double d) {
    speed = d;
}

void HostData::setAdditionalDegree(int i) {
    additionalDegree = i;
}

void HostData::setStreaming(bool b) {
    streaming = b;
}

void HostData::setVehicleConnected(bool b) {
    vehicleConnected = b;
}

void HostData::setPointChanging(bool b) {
    pointChanging = b;
}

void HostData::setGpsPosition(Point3D p) {
    gpsPosition = p;
}

void HostData::setGravitationalField(Point3D p) {
    gravitationalField = p;
}

void HostData::setMagneticField(Point3D p) {
    magneticField = p;
}

void HostData::setPreviousGpsPosition(Point3D p) {
    previousGpsPosition = p;
}

void HostData::setPreviousGravitationalField(Point3D p) {
    previousGravitationalField = p;
}

void HostData::setPreviousMagneticField(Point3D p) {
    previousMagneticField = p;
}

void HostData::update(Message* msg) {
    BatteryPartialHostData* bapd = dynamic_cast<BatteryPartialHostData*>(msg);
    BooleanPartialHostData* bopd = dynamic_cast<BooleanPartialHostData*>(msg);
    ControlPartialHostData* copd = dynamic_cast<ControlPartialHostData*>(msg);
    PointPartialHostData* popd = dynamic_cast<PointPartialHostData*>(msg);
    SpeedPartialHostData* sppd = dynamic_cast<SpeedPartialHostData*>(msg);
    if (bapd) bapd->apply(this);
    if (bopd) bopd->apply(this);
    if (copd) copd->apply(this);
    if (popd) popd->apply(this);
    if (sppd) sppd->apply(this);
}

void HostData::update(HostData* data) {
    setSpeed(data->getSpeed());
    setStreaming(data->isStreaming());
    setVehicleConnected(data->isVehicleConnected());
    setPointChanging(data->isPointChanging());
    setAdditionalDegree(data->getAdditionalDegree());
    setGpsPosition(data->getGpsPosition());
    setGravitationalField(data->getGravitationalField());
    setMagneticField(data->getMagneticField());
    setPreviousGpsPosition(data->getPreviousGpsPosition());
    setPreviousGravitationalField(data->getPreviousGravitationalField());
    setPreviousMagneticField(data->getPreviousMagneticField());
    BaseData<HostData>::update(data);
}
