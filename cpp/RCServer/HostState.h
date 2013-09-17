/* 
 * File:   HostState.h
 * Author: zoli
 *
 * Created on 2013. szeptember 17., 16:44
 */

#ifndef HOSTSTATE_H
#define	HOSTSTATE_H

#include "Point3D.h"

class HostState {
    
    public:
    
        HostState() {
            SPEED = -1;
            AZIMUTH = -1;
        }
        
        HostState(Point3D location, double speed, int azimuth) {
            LOCATION = location;
            SPEED = speed;
            AZIMUTH = azimuth;
        }
        
        void serialize(Message::Writer& w) {
            w.StartObject();
            if (LOCATION.isExists()) {
                w.String("LOCATION");
                LOCATION.serialize(w);
            }
            if (SPEED > 0) {
                w.String("SPEED");
                w.Double(SPEED);
            }
            if (AZIMUTH > 0) {
                w.String("AZIMUTH");
                w.Int(AZIMUTH);
            }
            w.EndObject();
        }
        
        void deserialize(Message::Value& v) {
            if (v.IsObject()) {
                if (v.HasMember("LOCATION")) {
                    LOCATION.deserialize(v);
                }
                else {
                    LOCATION.setExists(false);
                }
                if (v.HasMember("SPEED")) {
                    Message::Value& val = v["SPEED"];
                    if (val.IsDouble()) SPEED = val.GetDouble();
                }
                else {
                    SPEED = -1;
                }
                if (v.HasMember("AZIMUTH")) {
                    Message::Value& val = v["AZIMUTH"];
                    if (val.IsInt()) AZIMUTH = val.GetInt();
                }
                else {
                    AZIMUTH = -1;
                }
            }
        }
        
        Point3D LOCATION;
        double SPEED;
        int AZIMUTH;
    
};

#endif	/* HOSTSTATE_H */
