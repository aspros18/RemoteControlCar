/* 
 * File:   ControllerSideVideoProcess.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 15., 9:38
 */

#include "ControllerSideVideoProcess.h"
#include "SocketJpegListener.h"
#include "JpegStore.h"
#include "ControllerStorage.h"
#include "StorageList.h"
#include "HostStorage.h"

class ControllerSideVideoListener : public SocketJpegListener {
    
public:
    
    ControllerSideVideoListener(Socket* s) : SocketJpegListener(s) {
        cs = NULL;
    }
    
    std::string getKey() {
        if (cs == NULL) cs = (ControllerStorage*) StorageList::findControllerStorageByName(((SSLSocket*)s)->getClientName());
        if (cs == NULL) return "";
        HostStorage* s = (HostStorage*) cs->getHostStorage();
        if (s == NULL) return "";
        return s->getName();
    }
    
private:
    
    ControllerStorage* cs;
    
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
