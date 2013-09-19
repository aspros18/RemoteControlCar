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
        
        MessageProcess* getMessageProcess() {
            return msgProc;
        }
        
        void setMessageProcess(MessageProcess* p) {
            msgProc = p;
        }
        
        std::string getName() {
            return name;
        }
        
        virtual T* getSender() = 0;
        virtual T* getReceiver() = 0;
        
    private:
        
        std::string name;
        MessageProcess* msgProc;
        
};

#endif	/* STORAGE_H */
