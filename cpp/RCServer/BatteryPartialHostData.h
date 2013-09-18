/* 
 * File:   BatteryPartialHostData.h
 * Author: zoli
 *
 * Created on 2013. szeptember 18., 12:32
 */

#ifndef BATTERYPARTIALHOSTDATA_H
#define	BATTERYPARTIALHOSTDATA_H

#include "PartialBaseData.h"
#include "HostData.h"

class BatteryPartialHostData : public BatteryPartialBaseData<HostData> {
    
    public:
        
        BatteryPartialHostData();
        
        BatteryPartialHostData(int dat);
        
    private:
        
        REGISTER_DEC_TYPE(BatteryPartialHostData);
        
};

#endif	/* BATTERYPARTIALHOSTDATA_H */
