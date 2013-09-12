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

#include "JpegStreamer.h"
#include "JpegStore.h"
class TestListener : public JpegListener {
    Socket* s;
    
    public:
    TestListener(Socket* cs) : JpegListener("testkey") {
        s = cs;
    }
    
    void onChanged(std::string data, bool frame) {
        if (s->isClosed()) {
            JpegStore::removeListener(this); // TODO: a) after this call, new connections don't get onChange
        }                                    //       b) only one connection gets events at the same time
        std::ostream out(s->getBuffer());    //       c) optional: stop function and onChange bool return
        std::cout << data;
        out << data;
    }
    
};

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
    sigemptyset(&sigHandler.sa_mask);
    sigHandler.sa_flags = 0;
    sigaction(SIGINT, &sigHandler, NULL);
    sigaction(SIGTERM, &sigHandler, NULL);
}

void* runStream(void* v) {
    Socket* sock = (Socket*) v;
    JpegStreamer streamer("testkey");
    streamer.start(sock);
    pthread_exit(NULL);
}

int main(int argc, char** argv) {
    
    pthread_t handleThread;
    Socket sock("gw-fzoli", 9000);
    if (pthread_create(&handleThread, NULL,  runStream, &sock)) {
        std::cerr << "Thread could not be created.\n";
        sock.close();
        exit (1);
    }
    ServerSocket ss(8008);
    while (!ss.isClosed()) {
        Socket* cs = ss.accept();
        TestListener* listener = new TestListener(cs);
        JpegStore::addListener(listener);
    }
    pthread_join(handleThread, NULL);
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
