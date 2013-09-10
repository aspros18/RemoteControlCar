/* 
 * File:   TestHandler.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 7., 9:29
 */

#include "TestHandler.h"
#include "TestProcess.h"
#include "DisconnectProcess.h"
#include "ConnectionKeys.h"

#include <iostream>
#include <stdexcept>

using namespace ConnectionKeys;

TestHandler::TestHandler(SSLSocket* socket) : SSLHandler(socket) {
}

SSLSocketter* TestHandler::createProcess() {
    switch (getConnectionId()) {
        case KEY_CONN_DUMMY:
            return new TestProcess(this);
        case KEY_CONN_DISCONNECT:
            return new DisconnectProcess(this, DC_TIMEOUT1, DC_TIMEOUT2, DC_DELAY);
        default:
            return NULL;
    }
}

void TestHandler::init() {
    if (getDeviceId() == KEY_DEV_CONTROLLER || getDeviceId() == KEY_DEV_HOST) {
        throw std::runtime_error("Unsupported client");
    }
}

void TestHandler::onProcessNull() {
    std::cerr << "unknown request\n";
}

void TestHandler::onException(std::exception &ex) {
    std::cerr << "TestHandler::onException<" << ex.what() << ">\n";
}
