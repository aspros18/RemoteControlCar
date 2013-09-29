/* 
 * File:   Storage.h
 * Author: zoli
 *
 * Created on 2013. szeptember 19., 3:01
 */

#ifndef STORAGE_H
#define	STORAGE_H

#include "MessageProcess.h"

template<class T>
class Storage {
    
    public:
        
        Storage(MessageProcess* p) {
            msgProc = p;
            name = p->getSocket()->getClientName();
        }
        
        void setMessageProcess(MessageProcess* p) {
            msgProc = p;
        }
        
        void sendMessage(Message* msg) {
            if (getMessageProcess()) getMessageProcess()->sendMessage(msg);
        }
        
        std::string getName() {
            return name;
        }
        
        virtual T* getSender() = 0;
        virtual T* getReceiver() = 0;
        
    private:
        
        MessageProcess* getMessageProcess() {
            return msgProc;
        }
        
        std::string name;
        MessageProcess* msgProc;
        
};

#endif	/* STORAGE_H */
