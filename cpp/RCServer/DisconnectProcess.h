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
        
        DisconnectProcess(SSLHandler* handler, unsigned int timeout1Sec, unsigned int timeout2Sec, unsigned int waitTimeMs);
        virtual ~DisconnectProcess();
        
        void run();
        void callOnDisconnect(std::exception* ex);
        
    protected:
        
        virtual void onConnect();
        virtual void beforeAnswer();
        virtual void afterAnswer();
        virtual void afterTimeout();
        virtual void onTimeout(std::exception* ex);
        virtual void onDisconnect(std::exception* ex);
        
    private:
            
        Timer* timer;
        unsigned int waitTime;
        bool disconnected, timeout;
        
        void setTimeoutActive(bool b, std::exception* ex);
        void callOnTimeout(std::exception* ex);
        void callAfterAnswer();

};

#endif	/* DISCONNECTPROCESS_H */
