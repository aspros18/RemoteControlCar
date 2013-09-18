/* 
 * File:   HostList.h
 * Author: zoli
 *
 * Created on 2013. szeptember 18., 22:34
 */

#ifndef HOSTLIST_H
#define	HOSTLIST_H

#include "Data.h"
#include <vector>

class HostList : public Data<HostList> {
    
    public:
        
        typedef std::vector<std::string> HostVector;
        
        HostList();
        
        void update(HostList* data);
        
        void serialize(Writer& w);
        void deserialize(Document& d);
        
        HostVector& getHosts();
        
    private:
        
        REGISTER_DEC_TYPE(HostList);
        HostVector hosts;
        
};

#endif	/* HOSTLIST_H */
