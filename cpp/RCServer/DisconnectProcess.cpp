/* 
 * File:   DisconnectProcess.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 8., 23:46
 */

#include "DisconnectProcess.h"


class DisconnectTimer : public Timer {
    
    private:
        
        DisconnectProcess* dp;
        
    public:
        
        DisconnectTimer(DisconnectProcess* proc, long timeout2) : Timer(0, timeout2) {
            dp = proc;
        }
        
        void tick() {
            
        }
        
};

DisconnectProcess::DisconnectProcess(SSLHandler* handler, long timeout1, long timeout2) : SSLProcess(handler) {
    timer = new DisconnectTimer(this, timeout2);
    getSocket()->setTimeout(timeout1);
}

DisconnectProcess::~DisconnectProcess() {
    delete timer;
}

void DisconnectProcess::run() {
}
