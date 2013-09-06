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
                c.setTimeout(0);
                c << "Hello World!";
                c.close();
            }
            catch (CertificateException ex) {
                std::cerr << "Certificate error: " + ex.msg() + "\n";
            }
            catch (SSLSocketException ex) {
                std::cerr << "SSL error: " + ex.msg() + "\n";
            }
            catch (SocketException ex) {
                std::cerr << "Socket error: " + ex.msg() + "\n";
            }
        }
    }
    catch (SocketException ex) {
        std::cerr << "Server could not be created.";
        return EXIT_FAILURE;
    }
    return EXIT_SUCCESS;
}
