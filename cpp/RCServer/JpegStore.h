/* 
 * File:   JpegStore.h
 * Author: zoli
 *
 * Created on 2013. szeptember 12., 1:21
 */

#ifndef JPEGSTORE_H
#define	JPEGSTORE_H

#include <string>
#include <map>
#include <vector>
#include <pthread.h>

#include "JpegListener.h"

class JpegStore {

    public:
        
        static std::string getHeader(std::string key);
        static std::string getFrame(std::string key);
        static void setHeader(std::string key, std::string data);
        static void setFrame(std::string key, std::string data);
        static void addListener(JpegListener* l);
        static void removeListener(JpegListener* l);
        
    private:
        
        struct JpegFrame {
            std::string header;
            std::string data;
        };
        
        static std::string get(bool header, std::string key);
        static void set(bool header, std::string key, std::string data);
        static void fireListeners(std::string key, std::string data, bool frame);
        
        static pthread_mutex_t mutexData, mutexHeader, mutexListener;
        
        typedef std::pair<std::string, JpegFrame*> JpegPair;
        typedef std::map<std::string, JpegFrame*> JpegMap;
        typedef std::vector<JpegListener*> ListenerVector;
        
        static JpegMap frames;
        static ListenerVector listeners;

};

#endif	/* JPEGSTORE_H */
