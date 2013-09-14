/* 
 * File:   TestDisconnectProcess.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 14., 22:50
 */

#include "TestDisconnectProcess.h"
#include "ConnectionKeys.h"

#include <iostream>

using namespace ConnectionKeys;

TestDisconnectProcess::TestDisconnectProcess(SSLHandler* handler) : DisconnectProcess(handler, DC_TIMEOUT1, DC_TIMEOUT2, DC_DELAY) {
    ;
}

void TestDisconnectProcess::onConnect() {
    std::cout << "connected\n";
}

void TestDisconnectProcess::afterAnswer() {
    std::cout << "answer\n";
}

void TestDisconnectProcess::afterTimeout() {
    std::cout << "timeout over\n";
}

void TestDisconnectProcess::onTimeout(std::exception* ex) {
    std::cout << "timeout: " << ex->what() << "\n";
}

void TestDisconnectProcess::onDisconnect(std::exception* ex) {
    std::cout << "disconnected: " << ex->what() << "\n";
}
