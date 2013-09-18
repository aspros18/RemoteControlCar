/* 
 * File:   PartialHostList.h
 * Author: zoli
 *
 * Created on 2013. szeptember 18., 23:21
 */

#ifndef PARTIALHOSTLIST_H
#define	PARTIALHOSTLIST_H

#include "Data.h"
#include "HostList.h"

class PartialHostList : public PartialData<HostList, std::string> {
    
public:
    
    enum ChangeType { ADD, REMOVE };
    
    PartialHostList();
    PartialHostList(std::string, ChangeType type);
    
    void serialize(Writer& w);
    void deserialize(Document& d);
    
    void apply(HostList* data);
    
private:
    
    REGISTER_DEC_TYPE(PartialHostList);
    static const char* ChangeTypeStrings[];
    ChangeType type;
    
    static const char* toString(ChangeType t) {
        return ChangeTypeStrings[(int) t];
    }

    static ChangeType toType(const char* s) {
        std::string ss(s);
        for (int i = 0; i < 2; i++) {
            std::string si(ChangeTypeStrings[i]);
            if (ss == si) return (ChangeType) i;
        }
        return ADD;
    }
    
};

#endif	/* PARTIALHOSTLIST_H */
