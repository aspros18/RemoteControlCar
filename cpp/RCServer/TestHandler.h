/* 
 * File:   TestHandler.h
 * Author: zoli
 *
 * Created on 2013. szeptember 7., 9:29
 */

#ifndef TESTHANDLER_H
#define	TESTHANDLER_H

#include "SSLHandler.h"

class TestHandler : public SSLHandler {
    
    public:
        
        TestHandler(SSLSocket* socket);
        
    protected:
        
        SSLSocketter* createProcess();
        void init();
        void onException(std::exception &ex);
        void onProcessNull();
        
};

#endif	/* TESTHANDLER_H */

