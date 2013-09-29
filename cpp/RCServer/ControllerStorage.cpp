/* 
 * File:   ControllerStorage.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 23., 15:12
 */

#include "ControllerStorage.h"
#include "HostStorage.h"
#include "BooleanPartialControllerData.h"
#include "StorageList.h"
#include "ControlPartialHostData.h"
#include "ControlPartialControllerData.h"

#include <algorithm>

ControllerStorageSupport::ControllerStorageSupport(ControllerStorage* cs) {
    storage = cs;
}

ControllerStorageSender::ControllerStorageSender(ControllerStorage* cs) : ControllerStorageSupport(cs) {
    ;
}

void ControllerStorageSender::setControlling(bool b) {
    BooleanPartialControllerData d(b, BooleanPartialControllerData::CONTROLLING);
    storage->sendMessage(&d);
}

void ControllerStorageSender::setWantControl(bool b) {
    BooleanPartialControllerData d(b, BooleanPartialControllerData::WANT_CONTROLL);
    storage->sendMessage(&d);
}

ControllerStorageReceiver::ControllerStorageReceiver(ControllerStorage* cs) : ControllerStorageSupport(cs) {
    ;
}

void ControllerStorageReceiver::setControl(Control c) {
    setControl(c, false);
}

void ControllerStorageReceiver::setControl(Control c, bool force) {
    HostStorage* hs = (HostStorage*) storage->getHostStorage();
    if (hs && hs->getHostData().isVehicleConnected() && (force || hs->getOwner() == storage)) {
        hs->incControlCount();
        hs->getHostData().setControl(c);
        ControlPartialHostData hd(c);
        ControlPartialControllerData cd(c);
        storage->broadcastMessage(&cd, &hd, true);
    }
}

void ControllerStorageReceiver::setHostName(std::string hostName) {
    HostList ls = StorageList::createHostList(storage->getName());
    if (!hostName.empty() && std::find(ls.getHosts().begin(), ls.getHosts().end(), hostName) != ls.getHosts().end()) {
        HostStorage* store = (HostStorage*) StorageList::findHostStorageByName(hostName);
        storage->setHostStorage(store);
        ControllerData dat = storage->createControllerData();
        storage->sendMessage(&dat);
    }
    if (hostName.empty()) {
        if (storage->getHostStorage() && storage == ((HostStorage*)storage->getHostStorage())->getOwner()) {
            setWantControl(false, false);
        }
        storage->setHostStorage(NULL);
        storage->sendMessage(&ls);
    }
}

void ControllerStorageReceiver::setWantControl(bool b) {
    setWantControl(b, true);
}

void ControllerStorageReceiver::setWantControl(bool wantControl, bool fire) {
    HostStorage* hs = (HostStorage*) storage->getHostStorage();
    if (hs == NULL) return;
    ControllerStorage* oldOwner = (ControllerStorage*) hs->getOwner();
    if (wantControl && oldOwner && oldOwner == storage) return;
    if (!wantControl && oldOwner && oldOwner != storage) {
        storage->getSender()->setWantControl(false);
        return;
    }
    ControllerStorage* newOwner = wantControl ? storage : NULL;
    Control c = hs->getHostData().getControl();
    if (c.getX() != 0 || c.getY() != 0) setControl(Control(0, 0), true);
    hs->setOwner(newOwner);
    if (oldOwner && fire) {
        oldOwner->getSender()->setControlling(false);
        oldOwner->getSender()->setWantControl(false);
    }
    if (newOwner) {
        newOwner->getSender()->setControlling(true);
        newOwner->getSender()->setWantControl(true);
    }
}

ControllerStorage::ControllerStorage(MessageProcess* p) : Storage<ControllerData>(p) {
    sender = new ControllerStorageSender(this);
    receiver = new ControllerStorageReceiver(this);
    disconnectedHost = hostStorage = NULL;
}

ControllerStorage::~ControllerStorage() {
    delete sender;
    delete receiver;
}

ControllerData* ControllerStorage::getSender() {
    return sender;
}

ControllerData* ControllerStorage::getReceiver() {
    return receiver;
}

Storage<HostData>* ControllerStorage::getHostStorage() {
    return hostStorage;
}

void ControllerStorage::setHostStorage(Storage<HostData>* s) {
    HostStorage* oldStorage = (HostStorage*) hostStorage;
    HostStorage* newStorage = (HostStorage*) s;
    if (oldStorage) oldStorage->removeController(this);
    if (newStorage) newStorage->addController(this);
    hostStorage = s;
}

bool ControllerStorage::hasDisconnectedHost() {
    return disconnectedHost;
}

void ControllerStorage::storeDisconnectedHost() {
    disconnectedHost = getHostStorage();
}

void ControllerStorage::restoreDisconnectedHost() {
    if (disconnectedHost) {
        setHostStorage(disconnectedHost);
        disconnectedHost = NULL;
    }
}

void ControllerStorage::onCommand(Command* cmd) {
    if (getHostStorage() && ((HostStorage*) getHostStorage())->getOwner() == this) {
        getHostStorage()->sendMessage(cmd);
    }
}

HostState ControllerStorage::createHostState(Storage<HostData>* hs) {
    if (hs) {
        HostData d = ((HostStorage*) hs)->getHostData();
        return HostState(d.getGpsPosition(), d.getSpeed() == -1 ? -1 : d.getSpeed() * 3.6, -1);
    }
    return HostState();
}

ControllerData ControllerStorage::createControllerData() {
    HostStorage* s = (HostStorage*) getHostStorage();
    if (!s) return ControllerData();
    ControllerData d;
    d.setHostState(createHostState(s));
    d.setHostName(s->getName());
    d.setViewOnly(false);
    d.setConnected(s->isConnected());
    d.setControlling(s->getOwner() == this);
    d.setWantControl(false);
    d.setBatteryLevel(s->getHostData().getBatteryLevel());
    d.setVehicleConnected(s->getHostData().isVehicleConnected());
    d.setHostUnderTimeout(s->isUnderTimeout());
    d.setControl(s->getHostData().getControl());
    d.setFullX(s->getHostData().isFullX());
    d.setFullY(s->getHostData().isFullY());
    d.setUp2Date(s->getHostData().isUp2Date());
    d.setTimeout(-1);
    return d;
}

void ControllerStorage::broadcastMessage(Message* msgc, Message* msgh, bool skipMe) {
    Storage<HostData>* hs = getHostStorage();
    if (msgc && hs) {
        StorageList::ControllerStorageVector l = StorageList::getControllerStorages();
        for (StorageList::ControllerStorageVector::iterator it = l.begin(); it != l.end(); it++) {
            ControllerStorage* cs = (ControllerStorage*) *it;
            if (skipMe && cs == this) continue;
            if (hs == cs->getHostStorage()) cs->sendMessage(msgc);
        }
    }
    if (msgh && hs) {
        hs->sendMessage(msgh);
    }
}
