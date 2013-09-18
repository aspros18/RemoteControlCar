/* 
 * File:   SpeedPartialHostData.h
 * Author: zoli
 *
 * Created on 2013. szeptember 18., 16:42
 */

#ifndef SPEEDPARTIALHOSTDATA_H
#define	SPEEDPARTIALHOSTDATA_H

#include "Data.h"
#include "HostData.h"

class SpeedPartialHostData : public PartialData<HostData, double> {
    
    public:
        
        SpeedPartialHostData();
        SpeedPartialHostData(double d);
        
        void serialize(Writer& w);
        void deserialize(Document& d);
        
        virtual void apply(HostData* data);
        
    private:
        
        REGISTER_DEC_TYPE(SpeedPartialHostData);
        
};

#endif	/* SPEEDPARTIALHOSTDATA_H */
