/* 
 * File:   HostList.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 18., 22:34
 */

#include "HostList.h"

REGISTER_DEF_TYPE(HostList, org.dyndns.fzoli.rccar.model.controller.HostList);

HostList::HostList() {
    
}

HostList::HostVector& HostList::getHosts() {
    return hosts;
}

void HostList::update(HostList* data) {
    hosts.clear();
    hosts.insert(hosts.end(), data->hosts.begin(), data->hosts.end());
}

void HostList::serialize(Writer& w) {
    w.StartObject();
    w.String("HOSTS");
    w.StartArray();
    for (HostVector::iterator it = hosts.begin(); it != hosts.end(); it++) {
        w.String(it->c_str());
    }
    w.EndArray();
    w.EndObject();
}

void HostList::deserialize(Document& d) {
    if (d.IsObject() && d.HasMember("HOSTS")) {
        Message::Value& v = d["HOSTS"];
        if (v.IsArray()) {
            hosts.clear();
            for (SizeType i = 0; i < v.Size(); i++) {
                if (v[i].IsString()) hosts.push_back(std::string(v[i].GetString()));
            }
        }
    }
}
