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
        
        MessageProcess(SSLHandler* handler, bool delMsg = false);
        virtual ~MessageProcess();
        
        void sendMessage(Message* msg, bool wait = true);
        virtual void onException(std::exception& ex);
        virtual void onMessageSent(Message* msg);
        void run();
        
    protected:
        
        virtual void onStart();
        virtual void onMessage(Message* msg);
        virtual void onUnknownMessage(UnknownMessage* msg);
        virtual void onStop();
        
    private:
        
        SimpleWorker* worker;
        bool deleteMsg;
        
};

#endif	/* MESSAGEPROCESS_H */
