/* 
 * File:   SocketJpegListener.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 13., 12:53
 */

#include "SocketJpegListener.h"

#include "JpegStore.h"

#include <unistd.h>

SocketJpegListener::SocketJpegListener(Socket* cs, std::string key) : JpegListener(key) {
    s = cs;
    headerWrited = false;
//    JpegStore::addListener(this);
}

SocketJpegListener::~SocketJpegListener() {
//    JpegStore::removeListener(this);
}

void SocketJpegListener::wait() {
    while (waitHandler() && !s->isClosed()) {
        usleep(10);
    }
}

bool SocketJpegListener::waitHandler() {
    return true;
}

bool SocketJpegListener::onChanged(std::string data, bool frame) {
    if (s->isClosed()) {
        return false;
    }
    if ((frame && headerWrited) || (!frame && !headerWrited)) {
        if (!frame) headerWrited = true;
        try {
            s->write(data.c_str(), data.size());
        }
        catch (...) {
            s->close();
            return false;
        }
    }
    return true;
}
