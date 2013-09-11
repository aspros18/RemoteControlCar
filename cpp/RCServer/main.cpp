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

char* findBoundary(std::string &bstr, char* buffer, long int maxlen) {
    long int len = std::min<long int>(strlen(buffer), maxlen) - bstr.length();
    long int ind = -1;
    for (long int i = 0; i < len; i++) {
        if (buffer[i] == '\n') {
            if (ind == -1) {
                ind = i;
                continue;
            }
            char* buf = buffer + ind + 1;
            unsigned int len = i - ind;
            if (len == bstr.length()) {
                std::string fl(buf, len);
                if (fl.find(bstr) == 0) {
                    return buf;
                }
            }
            ind = i;
        }
    }
    return NULL;
}

int main(int argc, char** argv) {
//    std::string bstr("--BoundaryString\n");
//    char* buffer = (char*) "fbdfkjsfáé\nldűp\n--BoundaryString\ndsááűőödsdsddsdsdsd";
//    char* bsbuf = findBoundary(bstr, buffer, 1000);
//    if (bsbuf) std::cout << bsbuf;
//    exit(0);
//    
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
    
    int i = 0;
    std::string f;
    while (out.good() && in.good()) {
        l = "";
        std::getline(in, l);
        f = f + l + "\n";
        if (l == bs) {
            if (i % 2 != 0) {
                out << f;
            }
            f = "";
            i++;
        }
    }
    
    exit(0);
    
//    
//    long int bufsize = 1000, rlen;
//    char* buffer = new char[bufsize];
//    
//    while (in.good()) {
//        
//        rlen = in.readsome(buffer, bufsize);
//        if (rlen <= 0) break;
//        char* buf = findBoundary(bs, buffer, rlen);
//        if (buf) std::cout << "ok" << std::endl;
////        else std::cout << rlen << std::endl;
////        std::cout.write(buffer, rlen);
//    }
//    
//    delete[] buffer;
//    exit(0);
    
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
