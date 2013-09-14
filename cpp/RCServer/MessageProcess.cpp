/* 
 * File:   MessageProcess.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 13., 15:01
 */

#include "MessageProcess.h"

#include <unistd.h>
#include <vector>
#include <pthread.h>
#include <stdexcept>
#include <sstream>

#include "StringUtils.h"

class SimpleWorker {
    
    public:
        
        SimpleWorker(MessageProcess* p) : mutex(PTHREAD_MUTEX_INITIALIZER), cv(PTHREAD_COND_INITIALIZER) {
            started = false;
            proc = p;
        }
        
        void submit(Message* msg, bool wait) {
            pthread_mutex_lock(&mutex);
            queue.push_back(msg);
            if (wait) pthread_cond_wait(&cv, &mutex);
            pthread_mutex_unlock(&mutex);
        }
        
        void start() {
            if (started) return;
            started = true;
            pthread_t workerThread;
            if (pthread_create(&workerThread, NULL,  run, this)) {
                started = false;
                std::runtime_error ex("Worker thread could not be started.");
                if (proc) proc->onException(ex);
            }
        }
        
        void stop() {
            started = false;
        }
        
    private:
        
        bool started;
        std::vector<Message*> queue;
        pthread_mutex_t mutex;
        pthread_cond_t cv;
        MessageProcess* proc;
        
        static void* run(void* v) {
            SimpleWorker* w = (SimpleWorker*) v;
            std::ostream out(w->proc->getSocket()->getBuffer());
            while (w->started) {
                pthread_mutex_lock(&w->mutex);
                if (w->queue.size() == 0) {
                    pthread_mutex_unlock(&w->mutex);
                    usleep(20);
                    continue;
                }
                Message* msg = w->queue.at(0);
                w->queue.erase(w->queue.begin());
                out << MessageFactory::getInstanceName(msg) << "\r\n";
                out << msg->serialize() << "\r\n\r\n";
                pthread_cond_signal(&w->cv);
                pthread_mutex_unlock(&w->mutex);
            }
            pthread_exit(NULL);
        }
        
};

MessageProcess::MessageProcess(SSLHandler* handler) : SSLProcess(handler) {
    worker = new SimpleWorker(this);
}

MessageProcess::~MessageProcess() {
    delete worker;
}

void MessageProcess::onStart() {
    ;
}

void MessageProcess::onStop() {
    ;
}

void MessageProcess::onException(std::exception& ex) {
    ;
}

void MessageProcess::onMessage(Message* msg) {
    ;
}

void MessageProcess::sendMessage(Message* msg, bool wait) {
    if (msg && !getSocket()->isClosed()) {
        worker->submit(msg, wait);
    }
}

void MessageProcess::run() {
    worker->start();
    onStart();
    std::string line;
    std::istream in(getSocket()->getBuffer());
    while (!getSocket()->isClosed()) {
        std::getline(in, line);
        std::string name = StringUtils::trim(line);
        std::ostringstream lines;
        do {
            std::getline(in, line);
            line = StringUtils::trim(line);
            lines << line;;
        }
        while (!line.empty());
        Message* msg = MessageFactory::createInstance(name);
        if (msg) {
            msg->deserialize(lines.str());
            onMessage(msg);
        }
    }
    onStop();
    worker->stop();
}
