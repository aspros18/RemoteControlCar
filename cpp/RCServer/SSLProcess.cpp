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

int SSLProcess::getDeviceId() {
    return getHandler()->getDeviceId();
}

int SSLProcess::getConnectionId() {
    return getHandler()->getConnectionId();
}

SSLHandler* SSLProcess::getHandler() {
    return handler;
}
