/* 
 * File:   PartialBaseData.h
 * Author: zoli
 *
 * Created on 2013. szeptember 17., 20:41
 */

#ifndef PARTIALBASEDATA_H
#define	PARTIALBASEDATA_H

#include "Data.h"

template<class D>
class BatteryPartialBaseData  : public PartialData<D, int> {
    
    public:
        
        BatteryPartialBaseData() : PartialData<D, int>() {}
        
        BatteryPartialBaseData(int dat) : PartialData<D, int>(dat) {}
        
        void apply(D* dat) {
            dat->setBatteryLevel(BatteryPartialBaseData<D>::data);
        }
        
        void serialize(Message::Writer& w) {
            // TODO
        }
        
        void deserialize(Message::Document& d) {
            // TODO
        }
        
};

#endif	/* PARTIALBASEDATA_H */
