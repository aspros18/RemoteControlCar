/* 
 * File:   MessageProcess.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 13., 15:01
 */

#include "MessageProcess.h"

class SimpleWorker {
    
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
