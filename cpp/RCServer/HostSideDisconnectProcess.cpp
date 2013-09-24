/* 
 * File:   HostSideDisconnectProcess.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 24., 22:58
 */

#include "HostSideDisconnectProcess.h"
#include "ConnectionKeys.h"
#include "StorageList.h"

using namespace ConnectionKeys;

HostSideDisconnectProcess::HostSideDisconnectProcess(SSLHandler* handler) : DisconnectProcess(handler, DC_TIMEOUT1, DC_TIMEOUT2, DC_DELAY) {
    storage = NULL;
}

HostStorage* HostSideDisconnectProcess::getHostStorage() {
    if (storage) return storage;
    return storage = (HostStorage*) StorageList::findHostStorageByName(getSocket()->getClientName());
}

void HostSideDisconnectProcess::setTimeout(bool b) {
    HostStorage* hs = getHostStorage();
    if (hs && hs->isUnderTimeout() != b) hs->setUnderTimeout(b);
}

void HostSideDisconnectProcess::afterTimeout() {
    setTimeout(false);
}

void HostSideDisconnectProcess::onTimeout(std::exception* ex) {
    setTimeout(true);
}

void HostSideDisconnectProcess::onDisconnect(std::exception* ex) {
    DisconnectProcess::onDisconnect(ex);
    setTimeout(false);
}
