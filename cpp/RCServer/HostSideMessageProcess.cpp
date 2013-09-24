/* 
 * File:   HostSideMessageProcess.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 15., 10:29
 */

#include "HostSideMessageProcess.h"
#include "BooleanPartialHostData.h"
#include "Timer.h"

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
