/* 
 * File:   HostStorage.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 19., 3:09
 */

#include "HostStorage.h"

HostStorageSupport::HostStorageSupport(HostStorage* hs) {
    storage = hs;
}

HostStorageSender::HostStorageSender(HostStorage* hs) : HostStorageSupport(hs) {
    ;
}

HostStorageReceiver::HostStorageReceiver(HostStorage* hs) : HostStorageSupport(hs) {
    ;
}

HostStorage::HostStorage(MessageProcess* p) : Storage<HostData>(p) {
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
