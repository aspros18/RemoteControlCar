/* 
 * File:   HostStorage.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 19., 3:09
 */

#include "HostStorage.h"
#include "ControlPartialHostData.h"
#include "BooleanPartialHostData.h"
#include "StorageList.h"
#include "ControllerStorage.h"
#include "BooleanPartialControllerData.h"
#include "BatteryPartialControllerData.h"
#include "ControlPartialControllerData.h"
#include "HostStatePartialControllerData.h"

#include <algorithm>

HostStorageSupport::HostStorageSupport(HostStorage* hs) {
    storage = hs;
}

HostStorageSender::HostStorageSender(HostStorage* hs) : HostStorageSupport(hs) {
    ;
}

void HostStorageSender::setStreaming(bool b) {
    BooleanPartialHostData bd(b, BooleanPartialHostData::STREAMING);
    storage->sendMessage(&bd);
    storage->getHostData().setStreaming(b);
}

void HostStorageSender::setControl(Control c) {
    ControlPartialHostData cd(c);
    storage->sendMessage(&cd);
    storage->getHostData().setControl(c);
}

HostStorageReceiver::HostStorageReceiver(HostStorage* hs) : HostStorageSupport(hs) {
    ;
}

void HostStorageReceiver::setSpeed(double d) {
    storage->getHostData().setSpeed(d);
}

void HostStorageReceiver::setControl(Control c) {
    storage->getHostData().setControl(c);
    ControlPartialControllerData d(c);
    storage->broadcastMessage(&d);
}

void HostStorageReceiver::setVehicleConnected(bool b) {
    storage->getHostData().setVehicleConnected(b);
    BooleanPartialControllerData d(b, BooleanPartialControllerData::VEHICLE_CONNECTED);
    storage->broadcastMessage(&d);
}

void HostStorageReceiver::setUp2Date(bool b) {
    storage->getHostData().setUp2Date(b);
    BooleanPartialControllerData d(b, BooleanPartialControllerData::UP_2_DATE);
    storage->broadcastMessage(&d);
}

void HostStorageReceiver::setBatteryLevel(int l) {
    storage->getHostData().setBatteryLevel(l);
    BatteryPartialControllerData d(l);
    storage->broadcastMessage(&d);
}

void HostStorageReceiver::setGpsPosition(Point3D p) {
    storage->getHostData().setGpsPosition(p);
    broadcastHostState();
}

void HostStorageReceiver::setGravitationalField(Point3D p) {
    storage->getHostData().setGravitationalField(p);
    broadcastHostState();
}

void HostStorageReceiver::setMagneticField(Point3D p) {
    storage->getHostData().setMagneticField(p);
    broadcastHostState();
}

void HostStorageReceiver::broadcastHostState() {
    if (!storage->getHostData().isPointChanging()) {
        HostStatePartialControllerData d(ControllerStorage::createHostState(storage));
        storage->broadcastMessage(&d);
    }
}

HostStorage::HostStorage(MessageProcess* p) : Storage<HostData>(p), mutexControllers(PTHREAD_MUTEX_INITIALIZER) {
    sender = new HostStorageSender(this);
    receiver = new HostStorageReceiver(this);
}

HostStorage::~HostStorage() {
    delete sender;
    delete receiver;
}

bool HostStorage::isConnected() {
    return connected;
}

bool HostStorage::isUnderTimeout() {
    return underTimeout;
}

void HostStorage::setUnderTimeout(bool b) {
    underTimeout = b;
    BooleanPartialControllerData d(b, BooleanPartialControllerData::HOST_UNDER_TIMEOUT);
    broadcastMessage(&d);
}

void HostStorage::setConnected(bool b) {
    connected = b;
}

HostStorage::ControllerVector& HostStorage::getControllers() {
    return controllers;
}

HostData& HostStorage::getHostData() {
    return hostData;
}

HostData* HostStorage::getSender() {
    return sender;
}

HostData* HostStorage::getReceiver() {
    return receiver;
}

void HostStorage::addController(ControllerStorageType* cs) {
    pthread_mutex_lock(&mutexControllers);
    controllers.push_back(cs);
    bool first = controllers.size() == 1;
    pthread_mutex_unlock(&mutexControllers);
    if (first) getSender()->setStreaming(true);
}

void HostStorage::removeController(ControllerStorageType* cs) {
    if (owner && owner == cs) {
        cs->getReceiver()->setHostName("");
        owner = NULL;
    }
    pthread_mutex_lock(&mutexControllers);
    ControllerVector::iterator position = std::find(controllers.begin(), controllers.end(), cs);
    if (position != controllers.end()) controllers.erase(position);
    bool last = controllers.empty();
    pthread_mutex_unlock(&mutexControllers);
    if (last) getSender()->setStreaming(false);
}

void HostStorage::sendMessage(Message* msg) {
    getMessageProcess()->sendMessage(msg);
}

void HostStorage::broadcastMessage(Message* msg) {
    pthread_mutex_lock(&mutexControllers);
    StorageList::ControllerStorageVector l = StorageList::getControllerStorages();
    for (StorageList::ControllerStorageVector::iterator it = l.begin(); it != l.end(); it++) {
        ControllerStorage* cs = (ControllerStorage*) *it;
        HostStorage* hs = (HostStorage*) cs->getHostStorage();
        if (hs && this == hs) {
            cs->getMessageProcess()->sendMessage(msg);
        }
    }
    pthread_mutex_unlock(&mutexControllers);
}
