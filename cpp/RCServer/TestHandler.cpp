/* 
 * File:   TestHandler.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 7., 9:29
 */

#include "TestHandler.h"
#include "TestProcess.h"
#include "DisconnectProcess.h"

#include <iostream>
#include <stdexcept>

TestHandler::TestHandler(SSLSocket* socket) : SSLHandler(socket) {
}

SSLSocketter* TestHandler::createProcess() {
    switch (getConnectionId()) {
        case 5:
            return new TestProcess(this);
        default:
            return new DisconnectProcess(this, 1, 10, 250);
    }
}

void TestHandler::init() {
//    throw std::runtime_error("Remote error");
}

void TestHandler::onProcessNull() {
    ;
}

void TestHandler::onException(std::exception &ex) {
    std::cerr << "TestHandler::onException<" << ex.what() << ">\n";
}
