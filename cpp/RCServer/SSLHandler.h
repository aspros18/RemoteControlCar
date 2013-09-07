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
    
protected:
    
    virtual SSLSocketter* createProcess() = 0;
    
private:
    
    SSLSocket* socket;
    static void* run(void*);
    
};

#endif	/* SSLHANDLER_H */

