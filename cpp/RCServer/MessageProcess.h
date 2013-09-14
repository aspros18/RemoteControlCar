/* 
 * File:   MessageProcess.h
 * Author: zoli
 *
 * Created on 2013. szeptember 13., 15:01
 */

#ifndef MESSAGEPROCESS_H
#define	MESSAGEPROCESS_H

#include "SSLProcess.h"
#include "Message.h"

class SimpleWorker;

class MessageProcess : public SSLProcess {
    
    public:
        
        MessageProcess(SSLHandler* handler);
        virtual ~MessageProcess();
        
        void sendMessage(Message* msg, bool wait=true);
        virtual void onException(std::exception& ex);
        void run();
        
    protected:
        
        virtual void onStart();
        virtual void onMessage(Message* msg);
        virtual void onStop();
        
    private:
        
        SimpleWorker* worker;
        
};

#endif	/* MESSAGEPROCESS_H */
