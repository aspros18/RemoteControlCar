/* 
 * File:   DisconnectProcess.h
 * Author: zoli
 *
 * Created on 2013. szeptember 8., 23:46
 */

#ifndef DISCONNECTPROCESS_H
#define	DISCONNECTPROCESS_H

#include "SSLProcess.h"
#include "Timer.h"

class DisconnectProcess : public SSLProcess {
    
    public:
        
        DisconnectProcess(SSLHandler* handler, long timeout1, long timeout2, long waitTime);
        virtual ~DisconnectProcess();
        
        void run();
        void callOnDisconnect(std::exception* ex);
        
    protected:
        
        virtual void onConnect();
        virtual void beforeAnswer();
        virtual void afterAnswer();
        virtual void onTimeout();
        virtual void afterTimeout();
        virtual void onDisconnect(std::exception* ex);
        
    private:
            
        Timer* timer;
        long waitTime;
        bool disconnected, timeout;
        
        void setTimeoutActive(bool b, std::exception* ex);

};

#endif	/* DISCONNECTPROCESS_H */
