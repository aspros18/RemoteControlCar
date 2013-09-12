/* 
 * File:   JpegStore.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 12., 1:21
 */

#include "JpegStore.h"

JpegStore::JpegMap JpegStore::frames;
pthread_mutex_t JpegStore::mutexHeader = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t JpegStore::mutexData = PTHREAD_MUTEX_INITIALIZER;

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
        JpegPair pair(key, fr);
        frames.insert(pair);
    }
    if (header) fr->header = data;
    else fr->data = data;
    pthread_mutex_unlock(m);
}

std::string JpegStore::getHeader(std::string key) {
    return get(true, key);
}

std::string JpegStore::getData(std::string key) {
    return get(false, key);
}

void JpegStore::setHeader(std::string key, std::string data) {
    set(true, key, data);
}

void JpegStore::setData(std::string key, std::string data) {
    set(false, key, data);
}
