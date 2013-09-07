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
    try {
        c->write("Thanks\r\n");
        std::string msg;
        c->read(msg);
        std::cout << msg << "\n";
    }
    catch (SocketException ex) {
        std::cerr << "Socket error: " + ex.msg() + "\n";
    }
}
