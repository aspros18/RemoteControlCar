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
        
        bool isConnected();
        bool isUnderTimeout();
        void setUnderTimeout(bool b);
        void setConnected(bool b);
        
    private:
        
        HostData hostData;
        HostData* sender;
        HostData* receiver;
        ControllerStorageType* owner;
        ControllerVector controllers;
        pthread_mutex_t mutexControllers;
        bool connected;
        bool underTimeout;
        int controlCount;
        
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
        
        void setStreaming(bool b);
        void setControl(Control c);
        
};

class HostStorageReceiver : public HostStorageSupport {
    
    public:
        
        HostStorageReceiver(HostStorage* hs);
        
        void setSpeed(double d);
        void setControl(Control c);
        void setVehicleConnected(bool b);
        void setUp2Date(bool b);
        void setBatteryLevel(int l);
        void setGpsPosition(Point3D p);
        void setGravitationalField(Point3D p);
        void setMagneticField(Point3D p);
        
    private:
        
        void broadcastHostState();
        
};

#endif	/* HOSTSTORAGE_H */
