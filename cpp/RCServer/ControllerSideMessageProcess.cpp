/* 
 * File:   TestMessageProcess.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 14., 22:41
 */

#include "ControllerSideMessageProcess.h"
#include "ControllerData.h"

#include <iostream>

ControllerSideMessageProcess::ControllerSideMessageProcess(SSLHandler* handler) : MessageProcess(handler) {
    ;
}

void ControllerSideMessageProcess::onStart() {
    ControllerData testData;
    sendMessage(&testData);
}

void ControllerSideMessageProcess::onMessage(Message* msg) {
    delete msg;
}

void ControllerSideMessageProcess::onUnknownMessage(UnknownMessage* msg) {
    std::cout << "Unknown controller message: " << msg->getClassName() << " - " << msg->getDefinition() << std::endl;
    delete msg;
}
