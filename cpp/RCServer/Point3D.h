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
            exists = false;
            X = 0;
            Y = 0;
            Z = 0;
        }
        
        Point3D(double x, double y, double z) {
            exists = true;
            X = x;
            Y = y;
            Z = z;
        }
        
        bool isExists() {
            return exists;
        }
        
        void setExists(bool b) {
            exists = b;
        }
        
        void serialize(Message::Writer& w) {
            w.StartObject();
            if (exists) {
                w.String("X");
                w.Double(X);
                w.String("Y");
                w.Double(Y);
                w.String("Z");
                w.Double(Z);
            }
            w.EndObject();
        }
        
        void deserialize(Message::Value& v) {
            if (v.IsObject()) {
                exists = true;
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
            else {
                exists = false;
            }
        }
        
    private:
        
        double X, Y, Z;
        bool exists;
        
};

#endif	/* POINT3D_H */
