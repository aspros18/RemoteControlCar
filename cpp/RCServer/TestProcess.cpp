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
        std::cout << "conn id: " << getConnectionId() << "\n";
        c->write("Test OK\r\n");
        std::string msg;
        c->read(msg);
        std::cout << msg << "\n";
    }
    catch (SocketException &ex) {
        std::cerr << "Test Socket error: " + ex.msg() + "\n";
    }
}
