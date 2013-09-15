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

class IsListenerRemovable {

    public:
    
        IsListenerRemovable(std::string key, std::string data, bool frame): _data(data), _key(key), _frame(frame) {
            ;
        }

        bool operator()(JpegListener* l) const {
            return l->getKey() == _key && !l->onChanged(_data, _frame);
        }

    private:
        
        std::string _data, _key;
        bool _frame;
        
};

void JpegStore::addListener(JpegListener* l) {
    std::string key = l->getKey();
    std::string h = getHeader(key);
    std::string f = getFrame(key);
    pthread_mutex_lock(&mutexListener);
    bool b = true;
    if (!h.empty()) {
        b &= l->onChanged(h, false);
        if (b && !f.empty()) {
            b &= l->onChanged(f, true);
            if (b) b &= l->onChanged(f, true);
        }
    }
    if (b) listeners.push_back(l);
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
    listeners.erase(std::remove_if(listeners.begin(), listeners.end(), IsListenerRemovable(key, data, frame)), listeners.end());
    pthread_mutex_unlock(&mutexListener);
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
