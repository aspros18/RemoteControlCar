/* 
 * File:   HostStorage.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 19., 3:09
 */

#include "HostStorage.h"

#include <algorithm>

HostStorageSupport::HostStorageSupport(HostStorage* hs) {
    storage = hs;
}

HostStorageSender::HostStorageSender(HostStorage* hs) : HostStorageSupport(hs) {
    ;
}

HostStorageReceiver::HostStorageReceiver(HostStorage* hs) : HostStorageSupport(hs) {
    ;
}

HostStorage::HostStorage(MessageProcess* p) : Storage<HostData>(p), mutexControllers(PTHREAD_MUTEX_INITIALIZER) {
    sender = new HostStorageSender(this);
    receiver = new HostStorageReceiver(this);
}

HostStorage::~HostStorage() {
    delete sender;
    delete receiver;
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
    // TODO
    pthread_mutex_unlock(&mutexControllers);
}
