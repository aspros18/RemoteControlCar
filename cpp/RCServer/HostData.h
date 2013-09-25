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
        void update(Message* msg);
        
        double getSpeed();
        virtual void setSpeed(double d);
        int getAdditionalDegree();
        virtual void setAdditionalDegree(int i);
        bool isStreaming();
        virtual void setStreaming(bool b);
        bool isVehicleConnected();
        virtual void setVehicleConnected(bool b);
        bool isPointChanging();
        virtual void setPointChanging(bool b);
        Point3D getGpsPosition();
        virtual void setGpsPosition(Point3D p);
        Point3D getGravitationalField();
        virtual void setGravitationalField(Point3D p);
        Point3D getMagneticField();
        virtual void setMagneticField(Point3D p);
        Point3D getPreviousGpsPosition();
        virtual void setPreviousGpsPosition(Point3D p);
        Point3D getPreviousGravitationalField();
        virtual void setPreviousGravitationalField(Point3D p);
        Point3D getPreviousMagneticField();
        virtual void setPreviousMagneticField(Point3D p);
        
    private:
            
        REGISTER_DEC_TYPE(HostData);
        
        double speed;
        int additionalDegree;
        bool streaming, vehicleConnected, pointChanging;
        Point3D gpsPosition, gravitationalField, magneticField, previousGpsPosition, previousMagneticField, previousGravitationalField;
        
};

#endif	/* HOSTDATA_H */
