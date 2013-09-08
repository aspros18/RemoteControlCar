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
        
        DisconnectProcess(SSLHandler* handler, long timeout1, long timeout2);
        virtual ~DisconnectProcess();
        
        void run();
        
    private:
            
        Timer* timer;

};

#endif	/* DISCONNECTPROCESS_H */
