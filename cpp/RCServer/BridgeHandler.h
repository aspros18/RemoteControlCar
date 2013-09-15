/* 
 * File:   TestHandler.h
 * Author: zoli
 *
 * Created on 2013. szeptember 7., 9:29
 */

#ifndef TESTHANDLER_H
#define	TESTHANDLER_H

#include "SSLHandler.h"

class BridgeHandler : public SSLHandler {
    
    public:
        
        BridgeHandler(SSLSocket* socket);
        
    protected:
        
        SSLSocketter* createProcess();
        void init();
        void onException(std::exception &ex);
        void onProcessNull();
        
    private:
        
        bool isController();
        
};

#endif	/* TESTHANDLER_H */
