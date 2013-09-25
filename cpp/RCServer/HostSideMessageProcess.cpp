/* 
 * File:   HostSideMessageProcess.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 15., 10:29
 */

#include "HostSideMessageProcess.h"
#include "BooleanPartialHostData.h"
#include "Timer.h"
#include "PartialHostList.h"
#include "BooleanPartialControllerData.h"
#include "OfflineChangeablePartialControllerData.h"
#include "StorageList.h"
#include "ControllerStorage.h"

#include <iostream>

class HSMPTimer : public Timer {
    
    public:
        
        HSMPTimer(HostSideMessageProcess* p) : Timer(60, 0, true) {
            proc = p;
        }
        
        void tick() {
            if (!proc->storage) {
                proc->getHandler()->closeProcesses();
            }
        }
        
    private:
        
        HostSideMessageProcess* proc;
        
};

HostSideMessageProcess::HostSideMessageProcess(SSLHandler* handler) : MessageProcess(handler) {
    timer = new HSMPTimer(this);
    storage = NULL;
}

HostSideMessageProcess::~HostSideMessageProcess() {
    delete timer;
}

void HostSideMessageProcess::sendConnectionMessage(bool connected) {
    if (storage == NULL) return;
    if (connected) storage->getSender()->setStreaming(!storage->getControllers().empty());
    storage->setConnected(connected);
    PartialHostList msgLs(getSocket()->getClientName(), connected ? PartialHostList::ADD : PartialHostList::REMOVE);
    BooleanPartialControllerData msgConn(connected, BooleanPartialControllerData::CONNECTED);
    OfflineChangeableDatas offDat(storage->getHostData().isFullX(), storage->getHostData().isFullY(), storage->getHostData().isVehicleConnected(), storage->getHostData().isUp2Date(), ControllerStorage::createHostState(storage));
    OfflineChangeablePartialControllerData msgOffDat(offDat);
    StorageList::ControllerStorageVector controllers = StorageList::getControllerStorages();
    for (StorageList::ControllerStorageVector::iterator it = controllers.begin(); it != controllers.end(); it++) {
        ControllerStorage* cs = (ControllerStorage*) *it;
        if (cs->getHostStorage() == NULL) {
            cs->getMessageProcess()->sendMessage(&msgLs);
        }
        if (cs->getHostStorage() == storage) {
            cs->getMessageProcess()->sendMessage(&msgOffDat);
            cs->getMessageProcess()->sendMessage(&msgConn);
        }
    }
}

void HostSideMessageProcess::onStart() {
    BooleanPartialHostData d(true, BooleanPartialHostData::STREAMING);
    sendMessage(&d);
}

void HostSideMessageProcess::onMessage(Message* msg) {
    delete msg;
}

void HostSideMessageProcess::onUnknownMessage(UnknownMessage* msg) {
    std::cout << "Unknown host message: " << msg->getClassName() << " - " << msg->getDefinition() << std::endl;
    delete msg;
}
