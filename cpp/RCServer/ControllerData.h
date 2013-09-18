/* 
 * File:   ControllerData.h
 * Author: zoli
 *
 * Created on 2013. szeptember 16., 19:30
 */

#ifndef CONTROLLERDATA_H
#define	CONTROLLERDATA_H

#include "BaseData.h"
#include "HostState.h"

class ControllerData : public BaseData<ControllerData> {
    
    public:
        
        ControllerData();
        
        void serialize(Message::Writer& writer);
        void deserialize(Message::Document& d);
        void update(ControllerData* data);
        
        std::string getHostName();
        virtual void setHostName(std::string name);
        HostState getHostState();
        virtual void setHostState(HostState state);
        long getTimeout();
        virtual void setTimeout(long timeout);
        bool isHostUnderTimeout();
        virtual void setHostUnderTimeout(bool b);
        bool isVehicleConnected();
        virtual void setVehicleConnected(bool b);
        bool isControlling();
        virtual void setControlling(bool b);
        bool isWantControl();
        virtual void setWantControl(bool b);
        bool isViewOnly();
        virtual void setViewOnly(bool b);
        bool isConnected();
        virtual void setConnected(bool b);
        
    private:
        
        REGISTER_DEC_TYPE(ControllerData);
        std::string hostName;
        HostState hostState;
        bool hostUnderTimeout, vehicleConnected, controlling, wantControl, viewOnly, connected;
        long timeout;
        
};

#endif	/* CONTROLLERDATA_H */
