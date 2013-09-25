/* 
 * File:   HostSideMessageProcess.h
 * Author: zoli
 *
 * Created on 2013. szeptember 15., 10:29
 */

#ifndef HOSTSIDEMESSAGEPROCESS_H
#define	HOSTSIDEMESSAGEPROCESS_H

#include "MessageProcess.h"
#include "Timer.h"
#include "HostStorage.h"

class HSMPTimer;

class HostSideMessageProcess : public MessageProcess {
    
    friend class HSMPTimer;
    
    public:
        
        HostSideMessageProcess(SSLHandler* handler);
        virtual ~HostSideMessageProcess();
        void onMessage(Message* m);
        void onUnknownMessage(UnknownMessage* m);
        void onStart();
        
    private:
        
        Timer* timer;
        HostStorage* storage;
        
        void sendConnectionMessage(bool connected);
        
};

#endif	/* HOSTSIDEMESSAGEPROCESS_H */
