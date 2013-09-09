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
        
    protected:
        
//        virtual void onConnect() = 0;
//        virtual void beforeAnswer() = 0;
//        virtual void afterAnswer() = 0;
//        virtual void onTimeout() = 0;
//        virtual void afterTimeout() = 0;
//        virtual void onDisconnect() = 0;
        
    private:
            
        Timer* timer;
        long waitTime;
        bool disconnected, timeout;

};

#endif	/* DISCONNECTPROCESS_H */
