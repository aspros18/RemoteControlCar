/* 
 * File:   HostSideMessageProcess.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 15., 10:29
 */

#include "HostSideMessageProcess.h"

#include <iostream>

HostSideMessageProcess::HostSideMessageProcess(SSLHandler* handler) : MessageProcess(handler) {
}

void HostSideMessageProcess::onStart() {
    std::ostream out(getSocket()->getBuffer());
    out << "org.dyndns.fzoli.rccar.model.host.HostData$BooleanPartialHostData\r\n{\"type\":\"STREAMING\",\"data\":true}\r\n\r\n";
}

void HostSideMessageProcess::onMessage(Message* msg) {
    delete msg;
}
