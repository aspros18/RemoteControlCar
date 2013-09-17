/* 
 * File:   BatteryPartialControllerData.h
 * Author: zoli
 *
 * Created on 2013. szeptember 17., 20:56
 */

#ifndef BATTERYPARTIALCONTROLLERDATA_H
#define	BATTERYPARTIALCONTROLLERDATA_H

#include "PartialBaseData.h"
#include "ControllerData.h"

class BatteryPartialControllerData : public BatteryPartialBaseData<ControllerData> {
    
    public:
        
        BatteryPartialControllerData();
        
        BatteryPartialControllerData(int dat);
        
    private:
        
        REGISTER_DEC_TYPE(BatteryPartialControllerData);
        
};

#endif	/* BATTERYPARTIALCONTROLLERDATA_H */
