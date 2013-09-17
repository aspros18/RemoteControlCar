/* 
 * File:   TestMessageProcess.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 14., 22:41
 */

#include "ControllerSideMessageProcess.h"
#include "ControllerData.h"

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
