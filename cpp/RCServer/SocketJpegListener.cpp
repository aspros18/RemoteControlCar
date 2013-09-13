/* 
 * File:   SocketJpegListener.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 13., 12:53
 */

#include "SocketJpegListener.h"

#include "JpegStore.h"

#include <iostream>

SocketJpegListener::SocketJpegListener(Socket* cs, std::string key) : JpegListener(key) {
    s = cs;
    headerWrited = false;
    JpegStore::addListener(this);
}

void SocketJpegListener::onChanged(std::string data, bool frame) {
    if (s->isClosed()) {
        JpegStore::removeListener(this);
        return;
    }
    if ((frame && headerWrited) || (!frame && !headerWrited)) {
        if (!frame) headerWrited = true;
        try {
            s->write(data.c_str(), data.size());
//            std::ostream out(s->getBuffer());
//            out << data;
        }
        catch (...) {
            s->close();
        }
    }
}
