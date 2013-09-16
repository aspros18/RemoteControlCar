/* 
 * File:   main.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 1., 19:59
 */

#include <iostream>
#include <unistd.h>
#include <signal.h>
#include <stdlib.h>
#include <stdio.h>

#include "SSLServerSocket.h"
#include "CertificateException.h"
#include "Config.h"
#include "BridgeHandler.h"

SSLServerSocket *s = NULL;

void exitHandler(int signal) {
    if (s != NULL) s->close();
}

void createServerSocket(Config *c) {
    std::string CAfile = c->getCaFile();
    std::string CRTfile = c->getCertFile();
    std::string KEYfile = c->getKeyFile();
    char *KEYpass = (char *) c->getPassword().c_str();
    s = new SSLServerSocket(c->getPort(), CAfile.c_str(), CRTfile.c_str(), KEYfile.c_str(), KEYpass);
    s->setTimeout(3);
    
    struct sigaction sigHandler;
    sigHandler.sa_handler = exitHandler;
    sigHandler.sa_flags = 0;
    sigemptyset(&sigHandler.sa_mask);
    sigaction(SIGINT, &sigHandler, NULL);
    sigaction(SIGTERM, &sigHandler, NULL);
    
    struct sigaction sigPipeHandler;
    sigPipeHandler.sa_handler = SIG_IGN;
    sigPipeHandler.sa_flags = 0;
    sigemptyset(&sigPipeHandler.sa_mask);
    sigaction(SIGPIPE, &sigPipeHandler, NULL);
}

int main(int argc, char** argv) {
    Config c("bridge.conf");
    if (!c.isCorrect()) {
        std::cerr << "Incorrect config file.\n";
        return EXIT_FAILURE;
    }
    try {
        createServerSocket(&c);
        while (!s->isClosed()) {
            try {
                new BridgeHandler(s->accept());
            }
            catch (SocketException &ex) {
                if (s->isClosed()) return EXIT_SUCCESS;
                std::cerr << "Connection error: " + ex.msg() + "\n";
            }
        }
    }
    catch (SocketException &ex) {
        std::cerr << "Server could not be created.\n";
        return EXIT_FAILURE;
    }
    return EXIT_SUCCESS;
}
