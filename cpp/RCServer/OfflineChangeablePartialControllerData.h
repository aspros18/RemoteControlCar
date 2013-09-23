/* 
 * File:   OfflineChangeablePartialControllerData.h
 * Author: zoli
 *
 * Created on 2013. szeptember 23., 22:33
 */

#ifndef OFFLINECHANGEABLEPARTIALCONTROLLERDATA_H
#define	OFFLINECHANGEABLEPARTIALCONTROLLERDATA_H

#include "HostState.h"
#include "ControllerData.h"
#include "Data.h"

class OfflineChangeableDatas {
    
    public:
        
        bool fullX, fullY, vehicleConnected, up2date;
        HostState state;
        
        OfflineChangeableDatas();
        OfflineChangeableDatas(bool fullX, bool fullY, bool vehicleConnected, bool up2date, HostState state);
        
        void serialize(Message::Writer& w);
        void deserialize(Message::Value& v);
        
};

class OfflineChangeablePartialControllerData : public PartialData<ControllerData, OfflineChangeableDatas> {
    
    public:
        
        OfflineChangeablePartialControllerData();
        OfflineChangeablePartialControllerData(OfflineChangeableDatas d);
        
        void apply(ControllerData* dat);
        void serialize(Message::Writer& w);
        void deserialize(Message::Document& d);
        
    private:
        
        REGISTER_DEC_TYPE(OfflineChangeablePartialControllerData);
        
};

#endif	/* OFFLINECHANGEABLEPARTIALCONTROLLERDATA_H */
