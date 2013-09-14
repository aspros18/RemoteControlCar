/* 
 * File:   TestMessageProcess.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 14., 22:41
 */

#include "TestMessageProcess.h"
#include "TestMessage.h"

#include <iostream>

TestMessageProcess::TestMessageProcess(SSLHandler* handler) : MessageProcess(handler) {
    ;
}

void TestMessageProcess::onStart() {
    TestMessage msg("Hello Java!");
    sendMessage(&msg);
}

void TestMessageProcess::onMessage(Message* msg) {
    std::cout << msg->serialize() << std::endl;
    delete msg;
}
