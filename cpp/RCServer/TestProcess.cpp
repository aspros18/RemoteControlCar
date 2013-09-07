/* 
 * File:   TestProcess.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 7., 9:15
 */

#include "TestProcess.h"
#include "SocketException.h"

#include <iostream>

TestProcess::TestProcess(SSLHandler* handler) : SSLProcess(handler) {
}

void TestProcess::run() {
    SSLSocket* c = getSocket();
    c->setTimeout(1);
    try {
        std::cout << "device id: " << getConnectionId() << "\n";
        std::string msg;
        std::istream in(getSocket()->getBuffer());
        std::getline(in, msg);
        std::cout << msg << "\n";
        c->write("Test OK\r\n");
    }
    catch (SocketException ex) {
        std::cerr << "Test Socket error: " + ex.msg() + "\n";
    }
}
