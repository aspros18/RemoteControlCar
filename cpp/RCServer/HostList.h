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
        void update(Message* msg);
        
        void serialize(Writer& w);
        void deserialize(Document& d);
        
        HostVector& getHosts();
        void addHost(std::string s);
        void removeHost(std::string s);
        
    private:
        
        REGISTER_DEC_TYPE(HostList);
        HostVector hosts;
        pthread_mutex_t mutexHosts;
        
};

#endif	/* HOSTLIST_H */
