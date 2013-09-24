/* 
 * File:   HostSideDisconnectProcess.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 24., 22:58
 */

#include "HostSideDisconnectProcess.h"
#include "ConnectionKeys.h"

using namespace ConnectionKeys;

HostSideDisconnectProcess::HostSideDisconnectProcess(SSLHandler* handler) : DisconnectProcess(handler, DC_TIMEOUT1, DC_TIMEOUT2, DC_DELAY) {
    ;
}

void HostSideDisconnectProcess::afterTimeout() {
    ;
}

void HostSideDisconnectProcess::onTimeout(std::exception* ex) {
    ;
}

void HostSideDisconnectProcess::onDisconnect(std::exception* ex) {
    DisconnectProcess::onDisconnect(ex);
}
