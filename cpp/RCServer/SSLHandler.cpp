/* 
 * File:   SSLHandler.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 7., 8:13
 */

#include "SSLHandler.h"
#include "SSLProcess.h"

#include <iostream>

SSLHandler::SSLHandler(SSLSocket* socket) {
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

void* SSLHandler::run(void* v) {
    SSLHandler* h = (SSLHandler*) v;
    SSLProcess* p = (SSLProcess*) h->createProcess();
    p->run();
    h->getSocket()->close();
    pthread_exit(NULL);
}
