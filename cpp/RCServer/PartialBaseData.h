/* 
 * File:   PartialBaseData.h
 * Author: zoli
 *
 * Created on 2013. szeptember 17., 20:41
 */

#ifndef PARTIALBASEDATA_H
#define	PARTIALBASEDATA_H

#include "Data.h"
#include "Control.h"

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

template<class D>
class ControlPartialBaseData  : public PartialData<D, Control> {
    
    public:
        
        ControlPartialBaseData() : PartialData<D, Control>() {}
        
        ControlPartialBaseData(Control dat) : PartialData<D, Control>(dat) {}
        
        void apply(D* dat) {
            dat->setControl(ControlPartialBaseData<D>::data);
        }
        
        void serialize(Message::Writer& w) {
            w.StartObject();
            w.String("data");
            ControlPartialBaseData<D>::data.serialize(w);
            w.EndObject();
        }
        
        void deserialize(Message::Document& d) {
            if (d.IsObject() && d.HasMember("data")) {
                ControlPartialBaseData<D>::data.deserialize(d["data"]);
            }
        }
        
};

template <class D>
class BooleanPartialBaseData : public PartialData<D, bool> {
    
    public:
        
        enum BoolType { STREAMING, VEHICLE_CONNECTED, UP_2_DATE, CONNECTED, HOST_UNDER_TIMEOUT, CONTROLLING, WANT_CONTROLL, VIEW_ONLY };
        
        BooleanPartialBaseData() : PartialData<D, bool>() {}
        
        BooleanPartialBaseData(bool dat, BoolType type) : PartialData<D, bool>(dat) {
            BooleanPartialBaseData<D>::type = type;
        }
        
        void serialize(Message::Writer& w) {
            w.StartObject();
            w.String("data");
            w.Bool(BooleanPartialBaseData<D>::data);
            w.String("type");
            w.String(BooleanPartialBaseData<D>::toString(BooleanPartialBaseData<D>::type));
            w.EndObject();
        }
        
        void deserialize(Message::Document& d) {
            if (d.IsObject()) {
                if (d.HasMember("data")) {
                    Message::Value& v = d["data"];
                    if (v.IsBool()) BooleanPartialBaseData<D>::data = v.GetBool();
                }
                if (d.HasMember("type")) {
                    Message::Value& v = d["type"];
                    if (v.IsString()) BooleanPartialBaseData<D>::type = BooleanPartialBaseData<D>::toType(v.GetString());
                }
            }
        }
        
    protected:
        
        BoolType type;
        
    private:
        
        static const char* BoolTypeStrings[];
        
        static const char* toString(BoolType t) {
            return BoolTypeStrings[(int) t];
        }
        
        static BoolType toType(const char* s) {
            std::string ss(s);
            for (int i = 0; i < 8; i++) {
                std::string si(BoolTypeStrings[i]);
                if (ss == si) return (BoolType) i;
            }
            return STREAMING;
        }
        
};

template <class D> const char* BooleanPartialBaseData<D>::BoolTypeStrings[] = { "STREAMING", "VEHICLE_CONNECTED", "UP_2_DATE", "CONNECTED", "HOST_UNDER_TIMEOUT", "CONTROLLING", "WANT_CONTROLL", "VIEW_ONLY" };

#endif	/* PARTIALBASEDATA_H */
