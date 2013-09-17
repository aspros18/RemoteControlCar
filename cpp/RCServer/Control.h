/* 
 * File:   Control.h
 * Author: zoli
 *
 * Created on 2013. szeptember 17., 13:28
 */

#ifndef CONTROL_H
#define	CONTROL_H

#include "Message.h"

class Control {
    
    public:
        
        Control() {
            ;
        }
        
        Control(int x, int y) {
            mX = x;
            mY = y;
        }
        
        void serialize(Message::Writer& w) {
            w.StartObject();
            w.String("mX");
            w.Int(mX);
            w.String("mY");
            w.Int(mY);
            w.EndObject();
        }
        
        void deserialize(Message::Value& v) {
            if (v.IsObject()) {
                if (v.HasMember("mX")) {
                    Message::Value& x = v["mX"];
                    if (x.IsInt()) mX = x.GetInt();
                }
                if (v.HasMember("mY")) {
                    Message::Value& y = v["mY"];
                    if (y.IsInt()) mY = y.GetInt();
                }
            }
        }
        
    private:
        
        int mX, mY;
        
};

#endif	/* CONTROL_H */