/* 
 * File:   ControllerSideVideoProcess.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 15., 9:38
 */

#include "ControllerSideVideoProcess.h"
#include "SocketJpegListener.h"
#include "JpegStore.h"

class ControllerSideVideoListener : public SocketJpegListener {
public:
    ControllerSideVideoListener(Socket* s) : SocketJpegListener(s) {
        ;
    }
    
    std::string getKey() {
        return "host";
    }
    
};

ControllerSideVideoProcess::ControllerSideVideoProcess(SSLHandler* handler) : SSLProcess(handler) {
    ;
}

void ControllerSideVideoProcess::run() {
    ControllerSideVideoListener l(getSocket());
    JpegStore::addListener(&l);
    l.wait();
    JpegStore::removeListener(&l);
}
