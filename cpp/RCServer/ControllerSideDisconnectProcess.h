/* 
 * File:   ControllerSideDisconnectProcess.h
 * Author: zoli
 *
 * Created on 2013. szeptember 14., 22:50
 */

#ifndef CONTROLLERSIDEDISCONNECTPROCESS_H
#define	CONTROLLERSIDEDISCONNECTPROCESS_H

#include "DisconnectProcess.h"

class ControllerSideDisconnectProcess : public DisconnectProcess {
    
    public:

        ControllerSideDisconnectProcess(SSLHandler* handler);

        void afterTimeout();
        void onTimeout(std::exception* ex);
        void onDisconnect(std::exception* ex);
    
};

#endif	/* CONTROLLERSIDEDISCONNECTPROCESS_H */