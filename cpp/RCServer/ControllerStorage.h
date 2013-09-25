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
        void setHostStorage(Storage<HostData>* s);
        void broadcastMessage(Message* msgc, Message* msgh, bool skipMe);
        ControllerData createControllerData();
        
        static HostState createHostState(Storage<HostData>* hs);
        
    private:
        
        ControllerData* sender;
        ControllerData* receiver;
        Storage<HostData>* hostStorage;
        
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
        
        void setControlling(bool b);
        void setWantControl(bool b);
        
};

class ControllerStorageReceiver : public ControllerStorageSupport {
    
    public:
        
        ControllerStorageReceiver(ControllerStorage* cs);
        
        void setControl(Control c);
        void setHostName(std::string name);
        void setWantControl(bool b);
        
    private:
        
        void setControl(Control c, bool force);
        void setWantControl(bool b, bool fire);
        
};

#endif	/* CONTROLLERSTORAGE_H */
