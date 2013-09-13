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
        virtual ~MessageProcess();
        
        void sendMessage(std::string msg, bool wait=true);
        virtual void onException(std::exception& ex);
        void run();
        
    protected:
        
        virtual void onStart();
        virtual void onMessage(std::string msg);
        virtual void onStop();
        
    private:
        
        SimpleWorker* worker;
        
};

#endif	/* MESSAGEPROCESS_H */
