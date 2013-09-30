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
        
        SimpleWorker(MessageProcess* p) : mutex(PTHREAD_MUTEX_INITIALIZER)/*, mutexDestroy(PTHREAD_MUTEX_INITIALIZER)*/, cv(PTHREAD_COND_INITIALIZER) {
            started = false;
            proc = p;
        }
        
        virtual ~SimpleWorker() {
//            stop();
//            pthread_mutex_lock(&mutexDestroy);
//            pthread_mutex_unlock(&mutexDestroy);
        }
        
        void submit(Message* msg, bool wait) {
            pthread_mutex_lock(&mutex);
            if (started) {
                queue.push_back(msg);
                if (wait) pthread_cond_wait(&cv, &mutex);
            }
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
        
        bool running() {
            return started;
        }
        
    private:
        
        bool started;
        std::vector<Message*> queue;
        pthread_mutex_t mutex/*, mutexDestroy*/;
        pthread_cond_t cv;
        MessageProcess* proc;
        
        static void* run(void* v) {
            SimpleWorker* w = (SimpleWorker*) v;
//            pthread_mutex_lock(&w->mutexDestroy);
            std::ostream out(w->proc->getSocket()->getBuffer());
            while (w->started && out.good()) {
                pthread_mutex_lock(&w->mutex);
                if (w->queue.size() == 0) {
                    pthread_mutex_unlock(&w->mutex);
                    usleep(20);
                    continue;
                }
                Message* msg = w->queue.at(0);
                w->queue.erase(w->queue.begin());
                UnknownMessage* umsg = dynamic_cast<UnknownMessage*>(msg);
                if (umsg) {
                    if (!umsg->getClassName().empty() && !umsg->getDefinition().empty()) {
                        out << umsg->getClassName() << "\r\n";
                        out << umsg->getDefinition() << "\r\n\r\n";
                    }
                }
                else {
                    std::string name = MessageFactory::getInstanceName(msg);
                    if (!name.empty()) {
                        Message::Buffer b;
                        Message::Writer w(b);
                        msg->serialize(w);
                        std::string def = std::string(b.GetString());
                        if (!def.empty()) {
                            out << name << "\r\n";
                            out << def << "\r\n\r\n";
                        }
                    }
                }
                pthread_cond_signal(&w->cv);
                pthread_mutex_unlock(&w->mutex);
                w->proc->onMessageSent(msg);
            }
            w->started = false;
            pthread_mutex_lock(&w->mutex);
            pthread_cond_broadcast(&w->cv);
            pthread_mutex_unlock(&w->mutex);
//            pthread_mutex_unlock(&w->mutexDestroy);
            pthread_exit(NULL);
        }
        
};

MessageProcess::MessageProcess(SSLHandler* handler, bool delMsg) : SSLProcess(handler) {
    worker = new SimpleWorker(this);
    deleteMsg = delMsg;
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
    getHandler()->onException(ex);
}

void MessageProcess::onMessage(Message* msg) {
    delete msg;
}

void MessageProcess::onUnknownMessage(UnknownMessage* msg) {
    delete msg;
}

void MessageProcess::onMessageSent(Message* msg) {
    if (deleteMsg) delete msg;
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
    while (worker->running() && !getSocket()->isClosed() && in.good()) {
        std::getline(in, line);
        std::string name = StringUtils::trim(line);
        std::ostringstream lines;
        do {
            std::getline(in, line);
            line = StringUtils::trim(line);
            lines << line;
        }
        while (!line.empty() && in.good());
        Message* msg = MessageFactory::createInstance(name);
        if (msg && in.good()) {
            Message::Document d;
            if (d.Parse<0>(lines.str().c_str()).HasParseError()) {
                delete msg;
            }
            else {
                msg->deserialize(d);
                onMessage(msg);
            }
        }
        else {
            if (msg) {
                delete msg;
            }
            if (in.good() && !msg) {
                UnknownMessage* msg = new UnknownMessage(name, lines.str());
                onUnknownMessage(msg);
            }
        }
    }
    if (!getSocket()->isClosed()) {
        std::runtime_error ex(std::string(worker->running() ? "Input" : "Output") + " stream closed.");
        onException(ex);
    }
    onStop();
    worker->stop();
}
