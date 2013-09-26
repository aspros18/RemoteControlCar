/* 
 * File:   TestMessageProcess.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 14., 22:41
 */

#include "ControllerSideMessageProcess.h"
#include "ControllerData.h"
#include "StorageList.h"

#include <iostream>

ControllerSideMessageProcess::ControllerSideMessageProcess(SSLHandler* handler) : MessageProcess(handler) {
    storage = NULL;
}

void ControllerSideMessageProcess::onStart() {
    storage = (ControllerStorage*) StorageList::createControllerStorage(this);
    if (!storage->hasDisconnectedHost()) {
        HostList msg = StorageList::createHostList(getSocket()->getClientName());
        sendMessage(&msg);
    }
    else {
        storage->restoreDisconnectedHost();
        ControllerData msg = storage->createControllerData();
        sendMessage(&msg);
    }
}

void ControllerSideMessageProcess::onMessage(Message* msg) {        
    if (storage) storage->getReceiver()->update(msg);
    delete msg;
}

void ControllerSideMessageProcess::onUnknownMessage(UnknownMessage* msg) {
    if (Command::isCommand(msg)) {
        Command cmd(msg);
        if (storage) storage->onCommand(&cmd);
    }
    else {
        std::cout << "Unknown controller message: " << msg->getClassName() << " - " << msg->getDefinition() << std::endl;
    }
    delete msg;
}
