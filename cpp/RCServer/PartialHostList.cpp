/* 
 * File:   PartialHostList.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 18., 23:21
 */

#include "PartialHostList.h"

#include <algorithm>

REGISTER_DEF_TYPE(PartialHostList, org.dyndns.fzoli.rccar.model.controller.HostList$PartialHostList);
const char* PartialHostList::ChangeTypeStrings[] = { "ADD", "REMOVE" };

PartialHostList::PartialHostList() : PartialData<HostList, std::string>() {
    ;
}

PartialHostList::PartialHostList(std::string s, ChangeType type) : PartialData<HostList, std::string>(s) {
    PartialHostList::type = type;
}

void PartialHostList::serialize(Writer& w) {
    w.StartObject();
    w.String("data");
    w.String(data.c_str());
    w.String("type");
    w.String(PartialHostList::toString(type));
    w.EndObject();
}

void PartialHostList::deserialize(Document& d) {
    if (d.IsObject()) {
        if (d.HasMember("data")) {
            Message::Value& v = d["data"];
            if (v.IsString()) data = std::string(v.GetString());
        }
        if (d.HasMember("type")) {
            Message::Value& v = d["type"];
            if (v.IsString()) type = PartialHostList::toType(v.GetString());
        }
    }
}

void PartialHostList::apply(HostList* d) {
    switch (type) {
        case ADD:
            d->getHosts().push_back(data);
            break;
        case REMOVE:
            HostList::HostVector v = d->getHosts();
            HostList::HostVector::iterator position = std::find(v.begin(), v.end(), data);
            if (position != v.end()) v.erase(position);
    }
}
