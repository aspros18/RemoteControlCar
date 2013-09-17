/* 
 * File:   ControlPartialControllerData.h
 * Author: zoli
 *
 * Created on 2013. szeptember 17., 22:18
 */

#ifndef CONTROLPARTIALCONTROLLERDATA_H
#define	CONTROLPARTIALCONTROLLERDATA_H

#include "PartialBaseData.h"
#include "ControllerData.h"

class ControlPartialControllerData : public ControlPartialBaseData<ControllerData> {
    
    public:
        
        ControlPartialControllerData();
        
        ControlPartialControllerData(Control dat);
        
    private:
        
        REGISTER_DEC_TYPE(ControlPartialControllerData);
        
};

#endif	/* CONTROLPARTIALCONTROLLERDATA_H */
