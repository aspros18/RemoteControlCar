/* 
 * File:   SSLProcess.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 7., 8:10
 */

#include "SSLProcess.h"

SSLProcess::SSLProcess(SSLHandler* handler) {
    this->handler = handler;
}

SSLSocket* SSLProcess::getSocket() {
    return getHandler()->getSocket();
}

SSLHandler* SSLProcess::getHandler() {
    return handler;
}
