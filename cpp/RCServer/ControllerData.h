/* 
 * File:   ControllerData.h
 * Author: zoli
 *
 * Created on 2013. szeptember 16., 19:30
 */

#ifndef CONTROLLERDATA_H
#define	CONTROLLERDATA_H

#include "Data.h"

class ControllerData : public Data<ControllerData> {
    
    public:
        
        void serialize(Writer& writer);
        void deserialize(Document& d);
        void update(ControllerData* data);
        
    private:
        
        REGISTER_DEC_TYPE(ControllerData);
        
};

#endif	/* CONTROLLERDATA_H */
