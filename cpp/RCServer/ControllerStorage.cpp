/* 
 * File:   ControllerStorage.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 23., 15:12
 */

#include "ControllerStorage.h"

ControllerStorageSupport::ControllerStorageSupport(ControllerStorage* cs) {
    storage = cs;
}

ControllerStorageSender::ControllerStorageSender(ControllerStorage* cs) : ControllerStorageSupport(cs) {
    ;
}

ControllerStorageReceiver::ControllerStorageReceiver(ControllerStorage* cs) : ControllerStorageSupport(cs) {
    ;
}

ControllerStorage::ControllerStorage(MessageProcess* p) : Storage<ControllerData>(p) {
    sender = new ControllerStorageSender(this);
    receiver = new ControllerStorageReceiver(this);
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
    return NULL;
}

HostState ControllerStorage::createHostState(Storage<HostData>* hs) {
    HostState s;
    return s;
}
