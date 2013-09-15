/* 
 * File:   JpegStore.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 12., 1:21
 */

#include "JpegStore.h"

#include <algorithm>

JpegStore::JpegMap JpegStore::frames;
JpegStore::ListenerVector JpegStore::listeners;
pthread_mutex_t JpegStore::mutexListener = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t JpegStore::mutexHeader = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t JpegStore::mutexData = PTHREAD_MUTEX_INITIALIZER;

void JpegStore::addListener(JpegListener* l) {
    std::string key = l->getKey();
    std::string h = getHeader(key);
    std::string f = getFrame(key);
    pthread_mutex_lock(&mutexListener);
    listeners.push_back(l);
    if (!h.empty()) {
        l->onChanged(h, false);
        if (!f.empty()) {
            l->onChanged(f, true);
            l->onChanged(f, true);
        }
    }
    pthread_mutex_unlock(&mutexListener);
}

void JpegStore::removeListener(JpegListener* l) {
    pthread_mutex_lock(&mutexListener);
    ListenerVector::iterator position = std::find(listeners.begin(), listeners.end(), l);
    if (position != listeners.end()) listeners.erase(position);
    pthread_mutex_unlock(&mutexListener);
}

void JpegStore::fireListeners(std::string key, std::string data, bool frame) {
    pthread_mutex_lock(&mutexListener);
    ListenerVector v(listeners);
    pthread_mutex_unlock(&mutexListener);
    for(ListenerVector::size_type i = 0; i != v.size(); i++) {
        JpegListener* l = v[i];
        if (l->getKey() == key) {
            l->onChanged(data, frame);
        }
    }
}

std::string JpegStore::get(bool header, std::string key) {
    pthread_mutex_t* m = header ? &mutexHeader : &mutexData;
    pthread_mutex_lock(m);
    std::string s;
    JpegFrame* fr = frames[key];
    if (!fr) s = "";
    else s = header ? fr->header : fr->data;
    pthread_mutex_unlock(m);
    return s;
}

void JpegStore::set(bool header, std::string key, std::string data) {
    pthread_mutex_t* m = header ? &mutexHeader : &mutexData;
    pthread_mutex_lock(m);
    JpegFrame* fr = frames[key];
    if (!fr) {
        fr = new JpegFrame();
        frames[key] = fr;
    }
    if (header) fr->header = data;
    else fr->data = data;
    pthread_mutex_unlock(m);
    fireListeners(key, data, !header);
}

std::string JpegStore::getHeader(std::string key) {
    return get(true, key);
}

std::string JpegStore::getFrame(std::string key) {
    return get(false, key);
}

void JpegStore::setHeader(std::string key, std::string data) {
    set(true, key, data);
}

void JpegStore::setFrame(std::string key, std::string data) {
    set(false, key, data);
}
