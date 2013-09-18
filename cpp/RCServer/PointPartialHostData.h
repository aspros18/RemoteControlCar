/* 
 * File:   PointPartialHostData.h
 * Author: zoli
 *
 * Created on 2013. szeptember 18., 12:24
 */

#ifndef POINTPARTIALHOSTDATA_H
#define	POINTPARTIALHOSTDATA_H

#include "PartialBaseData.h"
#include "HostData.h"

class PointPartialHostData : public PointPartialBaseData<HostData> {
    
    public:
        
        PointPartialHostData();
        
        PointPartialHostData(Point3D dat, PointType type);
        
        void apply(HostData* data);
        
    private:
        
        REGISTER_DEC_TYPE(PointPartialHostData);
        
};

#endif	/* POINTPARTIALHOSTDATA_H */
