/* 
 * File:   PointPartialHostData.h
 * Author: zoli
 *
 * Created on 2013. szeptember 18., 12:24
 */

#ifndef POINTPARTIALHOSTDATA_H
#define	POINTPARTIALHOSTDATA_H

#include "HostData.h"
#include "PointData.h"
#include "Data.h"

#include <vector>

class PointPartialHostData : public PartialData<HostData, std::vector<PointData<HostData>>> {
    
    public:
        
        PointPartialHostData();
        
        void serialize(Message::Writer& w);
        void deserialize(Message::Document& d);
        
        void add(PointData<HostData> pd);
        void apply(HostData* data);
        
    private:
        
        REGISTER_DEC_TYPE(PointPartialHostData);
        
};

#endif	/* POINTPARTIALHOSTDATA_H */
