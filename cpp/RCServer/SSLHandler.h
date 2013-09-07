/* 
 * File:   SSLHandler.h
 * Author: zoli
 *
 * Created on 2013. szeptember 7., 8:13
 */

#ifndef SSLHANDLER_H
#define	SSLHANDLER_H

#include "SSLSocketter.h"

class SSLHandler : public SSLSocketter {
    
public:
    
    SSLHandler(SSLSocket* socket);
    
    SSLSocket* getSocket();
    int getDeviceId();
    int getConnectionId();
    
protected:
    
    virtual SSLSocketter* createProcess() = 0;
    virtual void init() = 0;
    virtual void onException(std::exception &ex) = 0;
    
private:
    
    SSLSocket* socket;
    int deviceId, connectionId;
    
    void runInit();
    void readStatus();
    static void* run(void*);
    static std::string VAL_OK;
    
};

#endif	/* SSLHANDLER_H */

