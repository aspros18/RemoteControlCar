/* 
 * File:   JpegStreamer.h
 * Author: zoli
 *
 * Created on 2013. szeptember 12., 13:05
 */

#ifndef JPEGSTREAMER_H
#define	JPEGSTREAMER_H

#include "Socket.h"

class JpegStreamer {

    public:
        
        JpegStreamer(std::string key="");
        
        void start(Socket* sock);
    
    protected:
        
        virtual std::string getKey();
        
    private:
    
        Socket* sock;
        std::string key;
        std::string bs;
        
        void readHeader();
        void readFrames();
        void readWhileGood();
        
};

#endif	/* JPEGSTREAMER_H */
