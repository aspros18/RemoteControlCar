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

class HostStorage : public Storage<HostData> {
    
    public:
        
        HostStorage(MessageProcess* p);
        virtual ~HostStorage();
        
        HostData* getSender();
        HostData* getReceiver();
        HostData& getHostData();
        
    private:
        
        HostData hostData;
        HostData* sender;
        HostData* receiver;
        
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
