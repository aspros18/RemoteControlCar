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
#include <pthread.h>

class JpegStore {

    public:
        
        static std::string getHeader(std::string key);
        static std::string getData(std::string key);
        static void setHeader(std::string key, std::string data);
        static void setData(std::string key, std::string data);
        
    private:
        
        struct JpegFrame {
            std::string header;
            std::string data;
        };
        
        static std::string get(bool header, std::string key);
        static void set(bool header, std::string key, std::string data);
        
        static pthread_mutex_t mutexData, mutexHeader;
        
        typedef std::pair<std::string, JpegFrame*> JpegPair;
        typedef std::map<std::string, JpegFrame*> JpegMap;
        static JpegMap frames;

};

#endif	/* JPEGSTORE_H */
