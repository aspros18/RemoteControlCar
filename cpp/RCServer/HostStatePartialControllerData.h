/* 
 * File:   HostStatePartialControllerData.h
 * Author: zoli
 *
 * Created on 2013. szeptember 23., 17:56
 */

#ifndef HOSTSTATEPARTIALCONTROLLERDATA_H
#define	HOSTSTATEPARTIALCONTROLLERDATA_H

#include "Data.h"
#include "ControllerData.h"
#include "HostState.h"

class HostStatePartialControllerData : public PartialData<ControllerData, HostState> {
    
    public:
        
        HostStatePartialControllerData();
        HostStatePartialControllerData(HostState d);
        
        void apply(ControllerData* data);
        void serialize(Writer& w);
        void deserialize(Document& d);
        
    private:
        
        REGISTER_DEC_TYPE(HostStatePartialControllerData);
        
};

#endif	/* HOSTSTATEPARTIALCONTROLLERDATA_H */
