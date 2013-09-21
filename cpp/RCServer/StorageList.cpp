/* 
 * File:   StorageList.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 20., 16:33
 */

#include "StorageList.h"
#include "HostStorage.h"

StorageList::HostStorageVector StorageList::hosts;
StorageList::ControllerStorageVector StorageList::controllers;

StorageList::HostStorageType* StorageList::findHostStorageByName(std::string name) {
    for (HostStorageVector::iterator it = hosts.begin(); it != hosts.end(); it++) {
        HostStorageType* s = *it;
        if (name == s->getName()) return s;
    }
    return NULL;
}

StorageList::ControllerStorageType* StorageList::findControllerStorageByName(std::string name) {
    for (ControllerStorageVector::iterator it = controllers.begin(); it != controllers.end(); it++) {
        ControllerStorageType* s = *it;
        if (name == s->getName()) return s;
    }
    return NULL;
}

StorageList::ControllerStorageType* StorageList::createControllerStorage(MessageProcess* p) {
    ControllerStorageType* s = findControllerStorageByName(p->getSocket()->getClientName());
    if (s == NULL) {
//        s = new ControllerStorage(p); // TODO
//        controllers.push_back(s);
    }
    else {
        s->setMessageProcess(p);
    }
    return s;
}

StorageList::HostStorageType* StorageList::createHostStorage(MessageProcess* p, HostData* d) {
    HostStorageType* s = findHostStorageByName(p->getSocket()->getClientName());
    if (s == NULL) {
        s = new HostStorage(p);
        hosts.push_back(s);
    }
    else {
        s->setMessageProcess(p);
    }
    HostStorage* hs = (HostStorage*) s;
    hs->getHostData().update(d);
    return s;
}

HostList StorageList::createHostList(std::string controllerName) {
    HostList l;
    HostList::HostVector ls = l.getHosts();
    for (HostStorageVector::iterator it = hosts.begin(); it != hosts.end(); it++) {
        HostStorage* s = (HostStorage*) *it;
//        if (s->isConnected()) {
//            ls.push_back(s->getName());
//        }
    }
    return l;
}
