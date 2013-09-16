/* 
 * File:   TestData.h
 * Author: zoli
 *
 * Created on 2013. szeptember 16., 19:30
 */

#ifndef TESTDATA_H
#define	TESTDATA_H

#include "Data.h"

class ControllerData : public Data<ControllerData> {
    
    public:
        
        std::string serialize();
        void deserialize(std::string json);
        void update(ControllerData* data);
        
    private:
        
        REGISTER_DEC_TYPE(ControllerData);
        
};

#endif	/* TESTDATA_H */
