/* 
 * File:   SSLHandler.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 7., 8:13
 */

#include "SSLHandler.h"
#include "SSLProcess.h"
#include "SocketException.h"

#include <iostream>

std::string SSLHandler::VAL_OK = "OK";

SSLHandler::SSLHandler(SSLSocket* socket) : deviceId(-1), connectionId(-1) {
    this->socket = socket;
    pthread_t handleThread;
    if (pthread_create(&handleThread, NULL,  run, this)) {
        std::cerr << "SSLHandler thread could not be created.\n";
        socket->close();
    }
}

SSLSocket* SSLHandler::getSocket() {
    return socket;
}

int SSLHandler::getDeviceId() {
    return deviceId;
}

int SSLHandler::getConnectionId() {
    return connectionId;
}

void SSLHandler::runInit() {
    try {
        init();
        try {
            getSocket()->write(VAL_OK.c_str());
            getSocket()->write("\r\n");
        }
        catch (SocketException ex) {
            throw ex;
        }
    }
    catch (std::exception ex) {
        getSocket()->write(ex.what());
        getSocket()->write("\r\n");
        throw ex;
    }
    catch (...) {
        std::runtime_error ex("unknown error");
        getSocket()->write(ex.what());
        getSocket()->write("\r\n");
        throw ex;
    }
}

void SSLHandler::readStatus() {
    std::string status;
    std::istream in(getSocket()->getBuffer());
    std::getline(in, status);
    if (!strcmp(status.c_str(), VAL_OK.c_str())) {
        throw std::runtime_error(status);
    }
}

void* SSLHandler::run(void* v) {
    SSLHandler* h = (SSLHandler*) v;
    SSLSocket * s = h->getSocket();
    try {
        h->deviceId = s->read();
        h->connectionId = s->read();
        h->runInit();
        h->readStatus();
    }
    catch (std::runtime_error ex) {
        h->onException(ex);
    }
    s->setTimeout(0);
    SSLProcess* p = (SSLProcess*) h->createProcess();
    p->run();
    s->close();
    pthread_exit(NULL);
}
