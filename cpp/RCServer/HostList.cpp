/* 
 * File:   HostList.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 18., 22:34
 */

#include "HostList.h"

#include <algorithm>

REGISTER_DEF_TYPE(HostList, org.dyndns.fzoli.rccar.model.controller.HostList);

HostList::HostList() : mutexHosts(PTHREAD_MUTEX_INITIALIZER) {
    ;
}

HostList::HostVector& HostList::getHosts() {
    return hosts;
}

void HostList::addHost(std::string s) {
    pthread_mutex_lock(&mutexHosts);
    hosts.push_back(s);
    pthread_mutex_unlock(&mutexHosts);
}

void HostList::removeHost(std::string s) {
    pthread_mutex_lock(&mutexHosts);
    HostVector::iterator position = std::find(hosts.begin(), hosts.end(), s);
    if (position != hosts.end()) hosts.erase(position);
    pthread_mutex_unlock(&mutexHosts);
}

void HostList::update(HostList* data) {
    pthread_mutex_lock(&mutexHosts);
    hosts.clear();
    hosts.insert(hosts.end(), data->hosts.begin(), data->hosts.end());
    pthread_mutex_unlock(&mutexHosts);
}

void HostList::serialize(Writer& w) {
    w.StartObject();
    w.String("HOSTS");
    w.StartArray();
    pthread_mutex_lock(&mutexHosts);
    for (HostVector::iterator it = hosts.begin(); it != hosts.end(); it++) {
        w.String(it->c_str());
    }
    pthread_mutex_unlock(&mutexHosts);
    w.EndArray();
    w.EndObject();
}

void HostList::deserialize(Document& d) {
    if (d.IsObject() && d.HasMember("HOSTS")) {
        Message::Value& v = d["HOSTS"];
        if (v.IsArray()) {
            pthread_mutex_lock(&mutexHosts);
            hosts.clear();
            for (SizeType i = 0; i < v.Size(); i++) {
                if (v[i].IsString()) hosts.push_back(std::string(v[i].GetString()));
            }
            pthread_mutex_unlock(&mutexHosts);
        }
    }
}
