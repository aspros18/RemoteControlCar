/* 
 * File:   MessageProcess.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 13., 15:01
 */

#include "MessageProcess.h"

#include <vector>
#include <pthread.h>
#include <stdexcept>

class SimpleWorker {
    
    public:
        
        SimpleWorker() : mutex(PTHREAD_MUTEX_INITIALIZER) {
            started = false;
        }
        
        void submit(std::string msg) {
            pthread_mutex_lock(&mutex);
            v.push_back(msg);
            pthread_mutex_unlock(&mutex);
        }
        
        void start() {
            if (started) return;
            started = true;
            pthread_t workerThread;
            if (pthread_create(&workerThread, NULL,  run, this)) {
                started = false;
                std::runtime_error ex("Worker thread could not be started.");
                onException(&ex);
            }
        }
        
        void stop() {
            started = false;
        }
        
    protected:
        
        virtual void onException(std::exception* ex) {
            ;
        }
        
    private:
        
        bool started;
        std::vector<std::string> v;
        pthread_mutex_t mutex;
        
        static void* run(void* v) {
            SimpleWorker* w = (SimpleWorker*) v;
            pthread_mutex_lock(&w->mutex);
            while (w->started) {
                // TODO
            }
            pthread_mutex_unlock(&w->mutex);
            pthread_exit(NULL);
        }
        
};

MessageProcess::MessageProcess(SSLHandler* handler) : SSLProcess(handler) {
    worker = new SimpleWorker();
}

void MessageProcess::onStart() {
    ;
}

void MessageProcess::onStop() {
    ;
}

void MessageProcess::onMessage(void* msg) {
    ;
}

void MessageProcess::sendMessage(void* msg, bool wait) {
    
}

void MessageProcess::run() {
    
}
