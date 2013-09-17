/* 
 * File:   Point3D.h
 * Author: zoli
 *
 * Created on 2013. szeptember 17., 13:32
 */

#ifndef POINT3D_H
#define	POINT3D_H

#include "Message.h"

class Point3D {
    
    public:
        
        Point3D() {
            ;
        }
        
        Point3D(double x, double y, double z) {
            X = x;
            Y = y;
            Z = z;
        }
        
        void serialize(Message::Writer& w) {
            w.StartObject();
            w.String("X");
            w.Double(X);
            w.String("Y");
            w.Double(Y);
            w.String("Z");
            w.Double(Z);
            w.EndObject();
        }
        
        void deserialize(Message::Value& v) {
            if (v.IsObject()) {
                if (v.HasMember("X")) {
                    Message::Value& x = v["X"];
                    if (x.IsDouble()) X = x.GetDouble();
                }
                if (v.HasMember("Y")) {
                    Message::Value& y = v["Y"];
                    if (y.IsDouble()) Y = y.GetDouble();
                }
                if (v.HasMember("Z")) {
                    Message::Value& z = v["Z"];
                    if (z.IsDouble()) Z = z.GetDouble();
                }
            }
        }
        
    private:
        
        double X, Y, Z;
        
};

#endif	/* POINT3D_H */
