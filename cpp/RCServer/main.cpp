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

SSLServerSocket *s = NULL;

void exitHandler(int signal){
    if (s != NULL) s->close();
    exit(0);
}

void createServerSocket(Config *c) {
    std::string CAfile = c->getCaFile();
    std::string CRTfile = c->getCertFile();
    std::string KEYfile = c->getKeyFile();
    char *KEYpass = (char *) c->getPassword().c_str();
    s = new SSLServerSocket(c->getPort(), CAfile.c_str(), CRTfile.c_str(), KEYfile.c_str(), KEYpass);
    struct sigaction sigIntHandler;
    sigIntHandler.sa_handler = exitHandler;
    sigemptyset(&sigIntHandler.sa_mask);
    sigIntHandler.sa_flags = 0;
    sigaction(SIGINT, &sigIntHandler, NULL);
    sigaction(SIGTERM, &sigIntHandler, NULL);
}

void* handle(void* vc) {
    SSLSocket* c = (SSLSocket*) vc;
    try {
        c->write("Thanks\r\n");
        std::string msg;
        c->read(msg);
        std::cout << msg << "\n";
    }
    catch (SocketException ex) {
        std::cerr << "Socket error: " + ex.msg() + "\n";
    }
    c->close();
    pthread_exit(NULL);
}

int main(int argc, char** argv) {
    Config c("bridge.conf");
    if (!c.isCorrect()) {
        std::cerr << "Incorrect config file.";
        return EXIT_FAILURE;
    }
    try {
        createServerSocket(&c);
        s->setTimeout(1);
        while (true) {
            try {
                SSLSocket c = s->accept();
                pthread_t handleThread;
                if (pthread_create(&handleThread, NULL, handle, &c)) {
                    std::cerr << "Thread could not be created.\n";
                    c.close();
                }
            }
            catch (SocketException ex) {
                std::cerr << "Connection error: " + ex.msg() + "\n";
            }
        }
    }
    catch (SocketException ex) {
        std::cerr << "Server could not be created.\n";
        return EXIT_FAILURE;
    }
    return EXIT_SUCCESS;
}
