/* 
 * File:   TestDisconnectProcess.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 14., 22:50
 */

#include "ControllerSideDisconnectProcess.h"
#include "ConnectionKeys.h"

using namespace ConnectionKeys;

ControllerSideDisconnectProcess::ControllerSideDisconnectProcess(SSLHandler* handler) : DisconnectProcess(handler, DC_TIMEOUT1, DC_TIMEOUT2, DC_DELAY) {
    ;
}

void ControllerSideDisconnectProcess::afterTimeout() {
    ;
}

void ControllerSideDisconnectProcess::onTimeout(std::exception* ex) {
    ;
}

void ControllerSideDisconnectProcess::onDisconnect(std::exception* ex) {
    DisconnectProcess::onDisconnect(ex);
}
