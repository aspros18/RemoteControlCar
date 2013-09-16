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
    TestMessage msg2("Hello World!");
    sendMessage(&msg2);
    std::ostream out(getSocket()->getBuffer());
    out << "org.dyndns.fzoli.rccar.model.controller.ControllerData\r\n{\"hostName\":\"host\",\"hostState\":{\"AZIMUTH\":105},\"hostUnderTimeout\":false,\"vehicleConnected\":false,\"controlling\":false,\"wantControl\":false,\"viewOnly\":false,\"connected\":true,\"CHAT_MESSAGES\":[],\"CONTROLLERS\":[{\"NAME\":\"controller\",\"lastModified\":\"Sep 15, 2013 10:21:50 AM\",\"controlling\":false,\"wantControl\":false}],\"fullX\":false,\"fullY\":false,\"up2date\":false,\"control\":{\"mX\":0,\"mY\":0}}\r\n\r\n";
}

void TestMessageProcess::onMessage(Message* msg) {
    std::cout << msg->serialize() << std::endl;
    delete msg;
}
