/* 
 * File:   JpegStreamer.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 12., 13:05
 */

#include "JpegStreamer.h"
#include "JpegStore.h"

#include <sstream>

JpegStreamer::JpegStreamer(std::string key) {
    this->key = key;
}

std::string JpegStreamer::getKey() {
    return key;
}

void JpegStreamer::start(Socket* sock) {
    this->sock = sock;
    readHeader();
    readFrames();
}

void JpegStreamer::readHeader() {
    std::string l; // line
    std::ostringstream h; // header lines
    std::istream in(sock->getBuffer());
    while (in.good()) {
        std::getline(in, l);
        h << l << std::endl;
        if (bs.empty() && l.find("Content-Type") == 0) {
            bs = l.substr(l.find("boundary=") + 9);
        }
        else if (!bs.empty()) {
            if (l == bs) break;
        }
    }
    JpegStore::setHeader(getKey(), h.str());
}

void JpegStreamer::readFrames() {
    while (!sock->isClosed()) {
        readWhileGood();
    }
}

void JpegStreamer::readWhileGood() {
    std::string f, l;
    std::istream in(sock->getBuffer());
    while (in.good()) {
        l = "";
        std::getline(in, l);
        f = f + l + "\n";
        if (l == bs) {
            JpegStore::setFrame(getKey(), f);
            f = "";
        }
    }
}
