/* 
 * File:   SSLHandler.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 7., 8:13
 */

#include "SSLHandler.h"
#include "SSLProcess.h"
#include "SocketException.h"
#include "StringUtils.h"

#include <iostream>
#include <algorithm>

std::string SSLHandler::VAL_OK = "OK";
std::vector<SSLSocketter*> SSLHandler::procs;
pthread_mutex_t SSLHandler::mutexProcs = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t SSLHandler::mutexInit = PTHREAD_MUTEX_INITIALIZER;

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
    status = StringUtils::trim(status);
    if (VAL_OK.compare(status)) {
        throw std::runtime_error(status);
    }
}

void SSLHandler::addProcess(SSLSocketter* p) {
    pthread_mutex_lock(&mutexProcs);
    procs.push_back(p);
    pthread_mutex_unlock(&mutexProcs);
}

void SSLHandler::removeProcess(SSLSocketter* p) {
    pthread_mutex_lock(&mutexProcs);
    std::vector<SSLSocketter*>::iterator position = std::find(procs.begin(), procs.end(), p);
    if (position != procs.end()) procs.erase(position);
    pthread_mutex_unlock(&mutexProcs);
}

void* SSLHandler::run(void* v) {
    SSLHandler* h = (SSLHandler*) v;
    SSLSocket * s = h->getSocket();
    pthread_mutex_lock(&mutexInit);
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
    if (p != NULL) {
        addProcess(p);
        pthread_mutex_unlock(&mutexInit);
        try {
            p->run();
        }
        catch (std::exception ex) {
            h->onException(ex);
        }
        removeProcess(p);
    }
    else {
        pthread_mutex_unlock(&mutexInit);
        h->onProcessNull();
    }
    s->close();
    pthread_exit(NULL);
}
