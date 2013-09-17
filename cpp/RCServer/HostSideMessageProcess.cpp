/* 
 * File:   HostSideMessageProcess.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 15., 10:29
 */

#include "HostSideMessageProcess.h"
#include "BooleanPartialHostData.h"

HostSideMessageProcess::HostSideMessageProcess(SSLHandler* handler) : MessageProcess(handler) {
}

void HostSideMessageProcess::onStart() {
    BooleanPartialHostData d(true, BooleanPartialHostData::STREAMING);
    sendMessage(&d);
}

void HostSideMessageProcess::onMessage(Message* msg) {
    delete msg;
}
