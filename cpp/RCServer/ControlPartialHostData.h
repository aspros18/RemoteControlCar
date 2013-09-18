/* 
 * File:   ControlPartialHostData.h
 * Author: zoli
 *
 * Created on 2013. szeptember 18., 16:38
 */

#ifndef CONTROLPARTIALHOSTDATA_H
#define	CONTROLPARTIALHOSTDATA_H

#include "PartialBaseData.h"
#include "HostData.h"

class ControlPartialHostData : public ControlPartialBaseData<HostData> {
    
    public:
        
        ControlPartialHostData();
        
        ControlPartialHostData(Control dat);
        
    private:
        
        REGISTER_DEC_TYPE(ControlPartialHostData);
        
};

#endif	/* CONTROLPARTIALHOSTDATA_H */
