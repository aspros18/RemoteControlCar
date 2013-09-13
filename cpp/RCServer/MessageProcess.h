/* 
 * File:   MessageProcess.h
 * Author: zoli
 *
 * Created on 2013. szeptember 13., 15:01
 */

#ifndef MESSAGEPROCESS_H
#define	MESSAGEPROCESS_H

#include "SSLProcess.h"

class SimpleWorker;

class MessageProcess : public SSLProcess {
    
    public:
        
        MessageProcess(SSLHandler* handler);
        
        void sendMessage(void* msg, bool wait=true);
        void run();
        
    protected:
        
        virtual void onStart();
        virtual void onMessage(void* msg);
        virtual void onStop();
        
    private:
        
        SimpleWorker* worker;
        
};

#endif	/* MESSAGEPROCESS_H */
