/* 
 * File:   HostData.h
 * Author: zoli
 *
 * Created on 2013. szeptember 18., 1:37
 */

#ifndef HOSTDATA_H
#define	HOSTDATA_H

#include "BaseData.h"
#include "Point3D.h"

class HostData : public BaseData<HostData> {
    
    public:
        
        HostData();
        
        void serialize(Message::Writer& writer);
        void deserialize(Message::Document& d);
        void update(HostData* data);
        
    private:
            
        REGISTER_DEC_TYPE(HostData);
        
        double speed;
        int additionalDegree;
        bool streaming, vehicleConnected, pointChanging;
        Point3D gpsPosition, gravitationalField, magneticField, previousGpsPosition, previousMagneticField, previousGravitationalField;
        
};

#endif	/* HOSTDATA_H */
