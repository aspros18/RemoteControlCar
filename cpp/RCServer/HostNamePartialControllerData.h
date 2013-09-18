/* 
 * File:   HostNamePartialControllerData.h
 * Author: zoli
 *
 * Created on 2013. szeptember 18., 23:22
 */

#ifndef HOSTNAMEPARTIALCONTROLLERDATA_H
#define	HOSTNAMEPARTIALCONTROLLERDATA_H

#include "Data.h"
#include "ControllerData.h"

class HostNamePartialControllerData : public PartialData<ControllerData, std::string> {
    
    public:
        
        HostNamePartialControllerData();
        HostNamePartialControllerData(std::string s);
        
        void apply(ControllerData* data);
        void serialize(Writer& w);
        void deserialize(Document& d);
    
    private:
        
        REGISTER_DEC_TYPE(HostNamePartialControllerData);

};

#endif	/* HOSTNAMEPARTIALCONTROLLERDATA_H */
