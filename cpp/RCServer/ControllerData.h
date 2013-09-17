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
        void setHostName(std::string name);
        HostState getHostState();
        void setHostState(HostState state);
        long getTimeout();
        void setTimeout(long timeout);
        bool isHostUnderTimeout();
        void setHostUnderTimeout(bool b);
        bool isVehicleConnected();
        void setVehicleConnected(bool b);
        bool isControlling();
        void setControlling(bool b);
        bool isWantControl();
        void setWantControl(bool b);
        bool isViewOnly();
        void setViewOnly(bool b);
        bool isConnected();
        void setConnected(bool b);
        
    private:
        
        REGISTER_DEC_TYPE(ControllerData);
        std::string hostName;
        HostState hostState;
        bool hostUnderTimeout, vehicleConnected, controlling, wantControl, viewOnly, connected;
        long timeout;
        
};

#endif	/* CONTROLLERDATA_H */
