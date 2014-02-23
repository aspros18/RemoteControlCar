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
SSLHandler::ProcessVector SSLHandler::PROCS;
pthread_mutex_t SSLHandler::mutexProcs = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t SSLHandler::mutexInit = PTHREAD_MUTEX_INITIALIZER;

SSLHandler::SSLHandler(SSLSocket* socket) : deviceId(-1), connectionId(-1) {
    this->socket = socket;
    pthread_t handleThread;
    if (pthread_create(&handleThread, NULL,  run, this)) {
        std::cerr << "SSLHandler thread could not be created.\n";
        socket->close();
        delete socket;
    }
}

SSLHandler::~SSLHandler() {

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

void SSLHandler::init() {
    std::string serverName(getSocket()->getServerName());
    std::string clientName(getSocket()->getClientName());
    if (!serverName.compare(clientName)) {
        throw std::runtime_error("The client uses the server's name");
    }
    for(ProcessVector::size_type i = 0; i != PROCS.size(); i++) {
        SSLProcess *p = (SSLProcess*) PROCS[i];
        if (p->getSocket()->isClosed()) continue;
        if (equals(this, p->getHandler())) {
            throw std::runtime_error("Duplicated certificate");
        }
    }
}

bool SSLHandler::equals(SSLHandler* h1, SSLHandler* h2, bool chkConnId) {
    std::string name1(h1->getSocket()->getClientName());
    std::string name2(h2->getSocket()->getClientName());
    return !name1.compare(name2) && h1->getDeviceId() == h2->getDeviceId() && (!chkConnId || h1->getConnectionId() == h2->getConnectionId());
}

void SSLHandler::runInit() {
    bool w = true;
    try {
        init();
        try {
            getSocket()->write(VAL_OK.c_str());
            getSocket()->write("\r\n");
        }
        catch (SocketException &ex) {
            w = false;
            throw;
        }
    }
    catch (std::exception &ex) {
        if (w) {
            getSocket()->write(ex.what());
            getSocket()->write("\r\n");
        }
        throw;
    }
    catch (...) {
        std::runtime_error ex("unknown error");
        getSocket()->write(ex.what());
        getSocket()->write("\r\n");
        throw;
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
    PROCS.push_back(p);
    pthread_mutex_unlock(&mutexProcs);
}

void SSLHandler::removeProcess(SSLSocketter* p) {
    pthread_mutex_lock(&mutexProcs);
    ProcessVector::iterator position = std::find(PROCS.begin(), PROCS.end(), p);
    if (position != PROCS.end()) PROCS.erase(position);
    pthread_mutex_unlock(&mutexProcs);
}

void SSLHandler::closeProcesses(SSLHandler* h) {
    pthread_mutex_lock(&mutexInit);
    pthread_mutex_lock(&mutexProcs);
    for(ProcessVector::size_type i = 0; i != PROCS.size(); i++) {
        SSLProcess *p = (SSLProcess*) PROCS[i];
        if (equals(p->getHandler(), h, false)) {
            p->getSocket()->close();
        }
    }
    pthread_mutex_unlock(&mutexProcs);
    pthread_mutex_unlock(&mutexInit);
}

void SSLHandler::closeProcesses() {
    SSLHandler::closeProcesses(this);
}

void SSLHandler::onException(std::exception &ex) {
    ;
}

void* SSLHandler::run(void* v) {
    SSLHandler* h = (SSLHandler*) v;
    SSLSocket * s = h->getSocket();
    try {
        h->deviceId = s->read();
        h->connectionId = s->read();
        pthread_mutex_lock(&mutexInit);
        h->runInit();
        h->readStatus();
    }
    catch (std::exception &ex) {
        pthread_mutex_unlock(&mutexInit);
        h->onException(ex);
        pthread_exit(NULL);
    }
    s->setTimeout(0);
    SSLProcess* p = (SSLProcess*) h->createProcess();
    if (p != NULL) {
        addProcess(p);
        pthread_mutex_unlock(&mutexInit);
        try {
            p->run();
        }
        catch (std::exception &ex) {
            h->onException(ex);
        }
        removeProcess(p);
        delete p;
    }
    else {
        pthread_mutex_unlock(&mutexInit);
        h->onProcessNull();
    }
    s->close();
    delete h;
    delete s;
    pthread_exit(NULL);
}
