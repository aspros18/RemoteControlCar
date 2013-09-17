/* 
 * File:   BaseData.h
 * Author: zoli
 *
 * Created on 2013. szeptember 17., 11:37
 */

#ifndef BASEDATA_H
#define	BASEDATA_H

#include "Data.h"
#include "Control.h"

template<class D>
class BaseData : public Data<D> {
    
    public:
        
        BaseData() : Data<D>() {
            batteryLevel = -1;
        }
        
        void serialize(Message::Writer& w) {
            w.String("control");
            control.serialize(w);
            w.String("up2date");
            w.Bool(up2date);
            w.String("fullX");
            w.Bool(fullX);
            w.String("fullY");
            w.Bool(fullY);
            if (batteryLevel > 0) {
                w.String("batteryLevel");
                w.Int(batteryLevel);
            }
        }
        
        void deserialize(Message::Document& d) {
            if (d.HasMember("control")) {
                control.deserialize(d["control"]);
            }
            if (d.HasMember("up2date")) {
                Message::Value& v = d["up2date"];
                if (v.IsBool()) up2date = v.GetBool();
            }
            if (d.HasMember("fullX")) {
                Message::Value& v = d["fullX"];
                if (v.IsBool()) fullX = v.GetBool();
            }
            if (d.HasMember("fullY")) {
                Message::Value& v = d["fullY"];
                if (v.IsBool()) fullY = v.GetBool();
            }
            if (d.HasMember("batteryLevel")) {
                Message::Value& v = d["batteryLevel"];
                if (v.IsInt()) batteryLevel = v.GetInt();
            }
        }
        
        void update(D* d) {
            setControl(d->getControl());
            setBatteryLevel(d->getBatteryLevel());
            setFullX(d->isFullX());
            setFullY(d->isFullY());
            setUp2Date(d->isUp2Date());
        }
        
        Control& getControl() {
            return control;
        }
        
        void setControl(Control& c) {
            control = c;
        }
        
        int getBatteryLevel() {
            return batteryLevel;
        }
        
        void setBatteryLevel(int l) {
            batteryLevel = l;
        }
        
        bool isFullX() {
            return fullX;
        }
        
        void setFullX(bool b) {
            fullX = b;
        }
        
        bool isFullY() {
            return fullY;
        }
        
        void setFullY(bool b) {
            fullY = b;
        }
        
        bool isUp2Date() {
            return up2date;
        }
        
        void setUp2Date(bool b) {
            up2date = b;
        }
        
    private:
        
        bool fullX, fullY, up2date;
        int batteryLevel;
        Control control;
        
};

#endif	/* BASEDATA_H */

