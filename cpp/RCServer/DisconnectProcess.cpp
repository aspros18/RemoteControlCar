/* 
 * File:   DisconnectProcess.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 8., 23:46
 */

#include "DisconnectProcess.h"

// TODO: befejezni az osztályt

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

DisconnectProcess::DisconnectProcess(SSLHandler* handler, long timeout1, long timeout2, long waitTime) : SSLProcess(handler) {
    timer = new DisconnectTimer(this, timeout2);
    this->waitTime = waitTime;
    getSocket()->setTimeout(timeout1);
}

DisconnectProcess::~DisconnectProcess() {
    delete timer;
}

void DisconnectProcess::run() {
    onConnect();
    try {
        while(!getSocket()->isClosed()) {
            getSocket()->write(1);
            if (getSocket()->read() != -1) {
                
            }
            usleep(waitTime);
        }
    }
    catch (std::exception &ex) {
        
    }
}
