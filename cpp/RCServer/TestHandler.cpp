/* 
 * File:   TestHandler.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 7., 9:29
 */

#include "TestHandler.h"
#include "TestProcess.h"

#include <iostream>
#include <stdexcept>

TestHandler::TestHandler(SSLSocket* socket) : SSLHandler(socket) {
}

SSLSocketter* TestHandler::createProcess() {
    return new TestProcess(this);
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
