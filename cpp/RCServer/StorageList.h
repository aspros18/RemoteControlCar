/* 
 * File:   StorageList.h
 * Author: zoli
 *
 * Created on 2013. szeptember 20., 16:33
 */

#ifndef STORAGELIST_H
#define	STORAGELIST_H

#include "Storage.h"
#include "HostData.h"
#include "ControllerData.h"
#include "HostList.h"
#include <vector>

class StorageList {
    
public:
    
    typedef Storage<HostData> HostStorageType;
    typedef Storage<ControllerData> ControllerStorageType;
    typedef std::vector<HostStorageType*> HostStorageVector;
    typedef std::vector<ControllerStorageType*> ControllerStorageVector;
    
    static HostStorageType* findHostStorageByName(std::string name);
    static ControllerStorageType* findControllerStorageByName(std::string name);
    static ControllerStorageType* createControllerStorage(MessageProcess* p);
    static HostStorageType* createHostStorage(MessageProcess* p, HostData* d);
    static HostList createHostList(std::string controllerName);
    
    static HostStorageVector& getHostStorages();
    static ControllerStorageVector& getControllerStorages();
    
private:
    
    static HostStorageVector hosts;
    static ControllerStorageVector controllers;
    
};

#endif	/* STORAGELIST_H */
