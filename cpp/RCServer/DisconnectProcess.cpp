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
        
    public:
        
        DisconnectTimer(DisconnectProcess* proc, long timeout2) : Timer(0, timeout2) {
            dp = proc;
        }
        
        void tick() {
            
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

void DisconnectProcess::run() {
//    onConnect();
    try {
        while(!getSocket()->isClosed()) {
            try {
                getSocket()->write(1);
                if (getSocket()->read() != -1) {
                    std::cout << "read ok\n";
                }
                else {
                    std::cout << "stream end\n";
                    getSocket()->close();
                }
            }
            catch (SocketException &ex) {
                switch (ex.cause()) {
                    case SocketException::read:
                        std::cerr << "read error - maybe timeout\n";
                        break;
                    case SocketException::write:
                        getSocket()->close();
                        std::cerr << "write error - socket closed\n";
                        break;
                    default:
                        throw;
                }
            }
            usleep(waitTime);
        }
    }
    catch (std::exception &ex) {
        std::cerr << ex.what() << "\n";
    }
}
