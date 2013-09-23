/* 
 * File:   ControllerStorage.h
 * Author: zoli
 *
 * Created on 2013. szeptember 23., 15:12
 */

#ifndef CONTROLLERSTORAGE_H
#define	CONTROLLERSTORAGE_H

#include "Storage.h"
#include "ControllerData.h"
#include "HostData.h"

class ControllerStorage : public Storage<ControllerData> {
    
    public:
        
        ControllerStorage(MessageProcess* p);
        virtual ~ControllerStorage();
        
        ControllerData* getSender();
        ControllerData* getReceiver();
        
        Storage<HostData>* getHostStorage();
        
    private:
        
        ControllerData* sender;
        ControllerData* receiver;
        
};

class ControllerStorageSupport : public ControllerData {
    
    public:
        
        ControllerStorageSupport(ControllerStorage* cs);
        
    protected:
        
        ControllerStorage* storage;
        
};

class ControllerStorageSender : public ControllerStorageSupport {
    
    public:
        
        ControllerStorageSender(ControllerStorage* cs);
        
};

class ControllerStorageReceiver : public ControllerStorageSupport {
    
    public:
        
        ControllerStorageReceiver(ControllerStorage* cs);
        
};

#endif	/* CONTROLLERSTORAGE_H */
