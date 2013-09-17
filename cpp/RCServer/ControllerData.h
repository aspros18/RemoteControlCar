/* 
 * File:   ControllerData.h
 * Author: zoli
 *
 * Created on 2013. szeptember 16., 19:30
 */

#ifndef CONTROLLERDATA_H
#define	CONTROLLERDATA_H

#include "BaseData.h"

class ControllerData : public BaseData<ControllerData> {
    
    public:
        
        ControllerData();
        
        void serialize(Message::Writer& writer);
        void deserialize(Message::Document& d);
        void update(ControllerData* data);
        
    private:
        
        REGISTER_DEC_TYPE(ControllerData);
        
};

#endif	/* CONTROLLERDATA_H */
