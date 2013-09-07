/* 
 * File:   TestHandler.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 7., 9:29
 */

#include "TestHandler.h"
#include "TestProcess.h"

TestHandler::TestHandler(SSLSocket* socket) : SSLHandler(socket) {
}

SSLSocketter* TestHandler::createProcess() {
    return new TestProcess(this);
}
