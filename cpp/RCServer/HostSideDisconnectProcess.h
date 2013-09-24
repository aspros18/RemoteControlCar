/* 
 * File:   HostSideDisconnectProcess.h
 * Author: zoli
 *
 * Created on 2013. szeptember 24., 22:58
 */

#ifndef HOSTSIDEDISCONNECTPROCESS_H
#define	HOSTSIDEDISCONNECTPROCESS_H

#include "DisconnectProcess.h"

class HostSideDisconnectProcess : public DisconnectProcess {
    
    public:
    
        HostSideDisconnectProcess(SSLHandler* handler);
        
        void afterTimeout();
        void onTimeout(std::exception* ex);
        void onDisconnect(std::exception* ex);
        
};

#endif	/* HOSTSIDEDISCONNECTPROCESS_H */
