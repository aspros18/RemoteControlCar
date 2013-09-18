/* 
 * File:   BooleanPartialControllerData.h
 * Author: zoli
 *
 * Created on 2013. szeptember 18., 13:00
 */

#ifndef BOOLEANPARTIALCONTROLLERDATA_H
#define	BOOLEANPARTIALCONTROLLERDATA_H

#include "PartialBaseData.h"
#include "ControllerData.h"

class BooleanPartialControllerData : public BooleanPartialBaseData<ControllerData> {
    
    public:
        
        BooleanPartialControllerData();
        
        BooleanPartialControllerData(bool dat, BoolType type);
        
        void apply(ControllerData* data);
        
    private:
        
        REGISTER_DEC_TYPE(BooleanPartialControllerData);
        
};

#endif	/* BOOLEANPARTIALCONTROLLERDATA_H */
