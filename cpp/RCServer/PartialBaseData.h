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
            w.StartObject();
            w.String("data");
            w.Int(BatteryPartialBaseData<D>::data);
            w.EndObject();
        }
        
        void deserialize(Message::Document& d) {
            if (d.IsObject() && d.HasMember("data")) {
                Message::Value& v = d["data"];
                if (v.IsInt()) BatteryPartialBaseData<D>::data = v.GetInt();
            }
        }
        
};

#endif	/* PARTIALBASEDATA_H */
