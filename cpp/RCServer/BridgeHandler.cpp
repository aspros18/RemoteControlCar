/* 
 * File:   TestHandler.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 7., 9:29
 */

#include "BridgeHandler.h"
#include "TestProcess.h"
#include "TestDisconnectProcess.h"
#include "TestMessageProcess.h"
#include "ConnectionKeys.h"
#include "ControllerSideVideoProcess.h"
#include "HostSideVideoProcess.h"
#include "HostSideMessageProcess.h"

#include <iostream>
#include <stdexcept>

using namespace ConnectionKeys;

BridgeHandler::BridgeHandler(SSLSocket* socket) : SSLHandler(socket) {
}

SSLSocketter* BridgeHandler::createProcess() {
    switch (getConnectionId()) {
        case KEY_CONN_DUMMY:
            return new TestProcess(this);
        case KEY_CONN_DISCONNECT:
            return new TestDisconnectProcess(this);
        case KEY_CONN_MESSAGE:
            if (isController()) return new TestMessageProcess(this);
            else return new HostSideMessageProcess(this);
        case KEY_CONN_VIDEO_STREAM:
            if (isController()) return new ControllerSideVideoProcess(this);
            else return new HostSideVideoProcess(this);
        default:
            return NULL;
    }
}

bool BridgeHandler::isController() {
    return KEY_DEV_PURE_CONTROLLER == getDeviceId();
}

void BridgeHandler::init() {
    if (getDeviceId() == KEY_DEV_CONTROLLER || getDeviceId() == KEY_DEV_HOST) {
        throw std::runtime_error("Unsupported client");
    }
    SSLHandler::init();
}

void BridgeHandler::onProcessNull() {
    std::cerr << "unknown request\n";
}

void BridgeHandler::onException(std::exception &ex) {
    std::cerr << "BridgeHandler::onException<" << ex.what() << "> " << getConnectionId() << std::endl;
}
