/* 
 * File:   BooleanPartialHostData.h
 * Author: zoli
 *
 * Created on 2013. szeptember 18., 1:33
 */

#ifndef BOOLEANPARTIALHOSTDATA_H
#define	BOOLEANPARTIALHOSTDATA_H

#include "PartialBaseData.h"
#include "HostData.h"

class BooleanPartialHostData : public BooleanPartialBaseData<HostData> {
    
    public:
        
        BooleanPartialHostData();
        
        BooleanPartialHostData(bool dat, BoolType type);
        
        void apply(HostData* data);
        
    private:
        
        REGISTER_DEC_TYPE(BooleanPartialHostData);
        
};

#endif	/* BOOLEANPARTIALHOSTDATA_H */
