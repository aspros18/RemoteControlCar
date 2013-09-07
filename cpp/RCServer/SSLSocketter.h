/* 
 * File:   SSLSocketter.h
 * Author: zoli
 *
 * Created on 2013. szeptember 7., 8:12
 */

#ifndef SSLSOCKETTER_H
#define	SSLSOCKETTER_H

#include "SSLSocket.h"

class SSLSocketter {
        
    protected:
    
        virtual SSLSocket* getSocket() = 0;
        virtual int getDeviceId() = 0;
        virtual int getConnectionId() = 0;
    
};

#endif	/* SSLSOCKETTER_H */
