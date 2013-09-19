/* 
 * File:   HostStorage.h
 * Author: zoli
 *
 * Created on 2013. szeptember 19., 3:09
 */

#ifndef HOSTSTORAGE_H
#define	HOSTSTORAGE_H

#include "Storage.h"
#include "HostData.h"
#include "ControllerData.h"

class HostStorage : public Storage<HostData> {
    
    public:
        
        typedef Storage<ControllerData> ControllerStorageType;
        typedef std::vector<ControllerStorageType*> ControllerVector;
        
        HostStorage(MessageProcess* p);
        virtual ~HostStorage();
        
        HostData* getSender();
        HostData* getReceiver();
        HostData& getHostData();
        
        void addController(ControllerStorageType*);
        void removeController(ControllerStorageType*);
        
        void sendMessage(Message* msg);
        void broadcastMessage(Message* msg);
        
    private:
        
        HostData hostData;
        HostData* sender;
        HostData* receiver;
        ControllerStorageType* owner;
        ControllerVector controllers;
        pthread_mutex_t mutexControllers;
        
};

class HostStorageSupport : public HostData {
    
    public:
        
        HostStorageSupport(HostStorage* hs);
        
    protected:
        
        HostStorage* storage;
        
};

class HostStorageSender : public HostStorageSupport {
    
    public:
        
        HostStorageSender(HostStorage* hs);
        
};

class HostStorageReceiver : public HostStorageSupport {
    
    public:
        
        HostStorageReceiver(HostStorage* hs);
        
};

#endif	/* HOSTSTORAGE_H */
