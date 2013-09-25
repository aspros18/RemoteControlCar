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
    storage->getMessageProcess()->sendMessage(&d);
}

void ControllerStorageSender::setWantControl(bool b) {
    BooleanPartialControllerData d(b, BooleanPartialControllerData::WANT_CONTROLL);
    storage->getMessageProcess()->sendMessage(&d);
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
        storage->getMessageProcess()->sendMessage(&dat);
    }
    if (hostName.empty()) {
        if (storage->getHostStorage() && storage == ((HostStorage*)storage->getHostStorage())->getOwner()) {
            setWantControl(false, false);
        }
        storage->setHostStorage(NULL);
        storage->getMessageProcess()->sendMessage(&ls);
    }
}

void ControllerStorageReceiver::setWantControl(bool b) {
    setWantControl(b, true);
}

void ControllerStorageReceiver::setWantControl(bool wantControl, bool fire) {
    if (storage->getHostStorage() == NULL) return;
    ControllerStorage* oldOwner = (ControllerStorage*) ((HostStorage*) storage->getHostStorage())->getOwner();
    if (wantControl && oldOwner && oldOwner == storage) return;
    if (!wantControl && oldOwner && oldOwner != storage) {
        storage->getSender()->setWantControl(false);
        return;
    }
    ControllerStorage* newOwner = wantControl ? storage : NULL;
    Control c = ((HostStorage*) storage->getHostStorage())->getHostData().getControl();
    if (c.getX() != 0 || c.getY() != 0) setControl(Control(0, 0), true);
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
    hostStorage = NULL;
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
    hostStorage = s;
}

HostState ControllerStorage::createHostState(Storage<HostData>* hs) {
    HostState s;
    // TODO
    return s;
}

ControllerData ControllerStorage::createControllerData() {
    ControllerData dat;
    // TODO
    return dat;
}

void ControllerStorage::broadcastMessage(Message* msgc, Message* msgh, bool skipMe) {
    Storage<HostData>* hs = getHostStorage();
    if (msgc && hs) {
        StorageList::ControllerStorageVector l = StorageList::getControllerStorages();
        for (StorageList::ControllerStorageVector::iterator it = l.begin(); it != l.end(); it++) {
            ControllerStorage* cs = (ControllerStorage*) *it;
            if (skipMe && cs == this) continue;
            if (hs == cs->getHostStorage()) cs->getMessageProcess()->sendMessage(msgc);
        }
    }
    if (msgh && hs) {
        hs->getMessageProcess()->sendMessage(msgh);
    }
}
