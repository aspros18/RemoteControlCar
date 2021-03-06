/* 
 * File:   SSLProcess.h
 * Author: zoli
 *
 * Created on 2013. szeptember 7., 8:10
 */

#ifndef SSLPROCESS_H
#define	SSLPROCESS_H

#include "SSLHandler.h"

class SSLProcess : public SSLSocketter {
    
    public:
        
        SSLProcess(SSLHandler* handler);
        virtual ~SSLProcess();
        
        virtual void run() = 0;
        SSLSocket* getSocket();
        SSLHandler* getHandler();
        int getDeviceId();
        int getConnectionId();
        
    private:
        
        SSLHandler* handler;
        
};

#endif	/* SSLPROCESS_H */

