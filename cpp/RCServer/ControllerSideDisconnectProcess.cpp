/* 
 * File:   TestDisconnectProcess.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 14., 22:50
 */

#include "ControllerSideDisconnectProcess.h"
#include "ConnectionKeys.h"
#include "HostStorage.h"
#include "StorageList.h"

using namespace ConnectionKeys;

ControllerSideDisconnectProcess::ControllerSideDisconnectProcess(SSLHandler* handler) : DisconnectProcess(handler, DC_TIMEOUT1, DC_TIMEOUT2, DC_DELAY) {
    prevCount = -1;
    storage = NULL;
}

ControllerStorage* ControllerSideDisconnectProcess::getControllerStorage() {
    if (storage) return storage;
    return storage = (ControllerStorage*) StorageList::findControllerStorageByName(getSocket()->getClientName());
}

void ControllerSideDisconnectProcess::afterTimeout() {
    ControllerStorage* cs = getControllerStorage();
    HostStorage* hs = (HostStorage*) (cs ? cs->getHostStorage() : NULL);
    if (cs && hs && prevCount != -1 && prevCount == hs->getControlCount()) {
        cs->getReceiver()->setControl(prevControl);
    }
}

void ControllerSideDisconnectProcess::onTimeout(std::exception* ex) {
    ControllerStorage* cs = getControllerStorage();
    if (cs) {
        HostStorage* hs = (HostStorage*) cs->getHostStorage();
        if (hs) {
            prevControl = hs->getHostData().getControl();
            prevCount = hs->getControlCount();
        }
        cs->getReceiver()->setControl(Control(0, 0));
    }
}

void ControllerSideDisconnectProcess::onDisconnect(std::exception* ex) {
    StorageList::freezeControllerStorage(this);
    ControllerStorage* cs = getControllerStorage();
    if (cs && cs->getHostStorage()) {
        cs->storeDisconnectedHost();
        cs->setHostStorage(NULL);
    }
    DisconnectProcess::onDisconnect(ex);
}
