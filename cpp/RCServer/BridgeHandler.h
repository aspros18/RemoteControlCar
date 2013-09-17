/* 
 * File:   BridgeHandler.h
 * Author: zoli
 *
 * Created on 2013. szeptember 7., 9:29
 */

#ifndef BRIDGEHANDLER_H
#define	BRIDGEHANDLER_H

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

#endif	/* BRIDGEHANDLER_H */
