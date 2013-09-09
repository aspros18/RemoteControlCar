/* 
 * File:   DisconnectProcess.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 8., 23:46
 */

#include "DisconnectProcess.h"
#include "SocketException.h"

#include <iostream>

// TODO: befejezni az osztályt

class DisconnectTimer : public Timer {
    
    private:
        
        DisconnectProcess* dp;
        std::exception* lastError;
        
    public:
        
        DisconnectTimer(DisconnectProcess* proc, long timeout2) : Timer(timeout2, 0, true) {
            dp = proc;
            lastError = NULL;
        }
        
        void start(std::exception* ex) {
            lastError = ex;
            Timer::start();
        }
        
        void tick() {
            dp->callOnDisconnect(lastError);
        }
        
};

DisconnectProcess::DisconnectProcess(SSLHandler* handler, long timeout1, long timeout2, long waitTime) : SSLProcess(handler) {
    timer = new DisconnectTimer(this, timeout2);
    getSocket()->setTimeout(timeout1 / 1000);
    this->waitTime = waitTime;
}

DisconnectProcess::~DisconnectProcess() {
    delete timer;
}

void DisconnectProcess::onConnect() {
}

void DisconnectProcess::beforeAnswer() {
}

void DisconnectProcess::afterAnswer() {
}

void DisconnectProcess::onTimeout() {
}

void DisconnectProcess::afterTimeout() {
}

void DisconnectProcess::onDisconnect(std::exception* ex) {
}

void DisconnectProcess::setTimeoutActive(bool b, std::exception* ex) {
    DisconnectTimer* dt = (DisconnectTimer*) timer;
    if (b) dt->start(ex);
    else dt->stop();
}

void DisconnectProcess::callOnDisconnect(std::exception* ex) {
    
}

void DisconnectProcess::run() {
    onConnect();
    try {
        while(!getSocket()->isClosed()) {
            try {
                getSocket()->write(1);
                if (getSocket()->read() != -1) {
                    std::cout << "read ok\n";
                }
                else {
                    throw std::runtime_error("socket closed");
                }
            }
            catch (SocketException &ex) {
                switch (ex.cause()) {
                    case SocketException::read:
                        std::cerr << "read error - maybe timeout\n";
                        break;
                    case SocketException::write:
                        throw std::runtime_error("socket closed");
                    default:
                        throw;
                }
            }
            usleep(waitTime);
        }
    }
    catch (std::exception &ex) {
        std::cout << "disconnect\n";
        std::cerr << ex.what() << "\n";
    }
}
