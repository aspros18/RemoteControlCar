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
#include "TestHandler.h"

#include <sstream>

SSLServerSocket *s = NULL;

void exitHandler(int signal){
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
    sigemptyset(&sigHandler.sa_mask);
    sigHandler.sa_flags = 0;
    sigaction(SIGINT, &sigHandler, NULL);
    sigaction(SIGTERM, &sigHandler, NULL);
}

void writeWhileGood(Socket* ss, Socket* cs, std::string& bs) {
//    int i = 0;
    std::string f, l;
    std::istream in(ss->getBuffer());
    std::ostream out(cs->getBuffer());
    while (out.good() && in.good()) {
        l = "";
        std::getline(in, l);
        f = f + l + "\n";
        if (l == bs) {
//            if (i % 2 != 0) {
                out << f;
//            }
            f = "";
//            i++;
        }
    }
}

int main(int argc, char** argv) {
    
    ServerSocket sss(8008);
    Socket* cs = sss.accept();
    std::ostream out(cs->getBuffer());
    
    Socket ss("gw-fzoli", 9000);
    ss.write("GET /\r\n\r\n");
    
    std::string l; // line
    std::string bs; // boundary string
    std::ostringstream h; // header
    std::istream in(ss.getBuffer());
    while (in.good()) {
        std::getline(in, l);
        h << l << std::endl;
        if (bs.empty() && l.find("Content-Type") == 0) {
            bs = l.substr(l.find("boundary=") + 9);
        }
        else if (!bs.empty()) {
            if (l == bs) break;
        }
    }
    
    out << h.str();
    
    while (!cs->isClosed()) {
        writeWhileGood(&ss, cs, bs);
    }
    
    exit(0);
    
    Config c("bridge.conf");
    if (!c.isCorrect()) {
        std::cerr << "Incorrect config file.\n";
        return EXIT_FAILURE;
    }
    try {
        createServerSocket(&c);
        while (!s->isClosed()) {
            try {
                new TestHandler(s->accept());
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
