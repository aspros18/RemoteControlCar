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
        virtual void onException(std::exception &ex);
        
};

#endif	/* TESTHANDLER_H */

