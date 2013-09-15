/* 
 * File:   HostSideVideoProcess.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 15., 9:37
 */

#include "HostSideVideoProcess.h"
#include "JpegStreamer.h"

HostSideVideoProcess::HostSideVideoProcess(SSLHandler* handler) : SSLProcess(handler) {
    ;
}

void HostSideVideoProcess::run() {
    JpegStreamer streamer(getSocket()->getClientName());
    streamer.start(getSocket());
}
