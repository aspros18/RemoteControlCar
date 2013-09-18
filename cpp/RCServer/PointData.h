/* 
 * File:   PointData.h
 * Author: zoli
 *
 * Created on 2013. szeptember 18., 13:38
 */

#ifndef POINTDATA_H
#define	POINTDATA_H

#include "Point3D.h"

template <class D>
class PointData {

    public:

        enum PointType { GPS_POSITION, GRAVITATIONAL_FIELD, MAGNETIC_FIELD };
        
        PointData() {}
        PointData(Point3D p, PointType t) : point(p), type(t) {}

        void serialize(Message::Writer& w) {
            w.StartObject();
            if (PointData<D>::point.isExists()) {
                PointData<D>::point.serialize(w);
            }
            w.String("type");
            w.String(PointData<D>::toString(PointData<D>::type));
            w.EndObject();
        }

        void deserialize(Message::Value& v) {
            if (v.IsObject()) {
                if (v.HasMember("point")) {
                    PointData<D>::point.deserialize(v["point"]);
                }
                else {
                    PointData<D>::point.setExists(false);
                }
                if (v.HasMember("type")) {
                    Message::Value& val = v["type"];
                    if (val.IsString()) PointData<D>::type = PointData<D>::toType(val.GetString());
                }
            }
        }

        Point3D point;
        PointType type;
        
    private:
        
        static const char* PointTypeStrings[];
        
        static const char* toString(PointType t) {
            return PointTypeStrings[(int) t];
        }
        
        static PointType toType(const char* s) {
            std::string ss(s);
            for (int i = 0; i < 8; i++) {
                std::string si(PointTypeStrings[i]);
                if (ss == si) return (PointType) i;
            }
            return GPS_POSITION;
        }

};

template <class D> const char* PointData<D>::PointTypeStrings[] = { "GPS_POSITION", "GRAVITATIONAL_FIELD", "MAGNETIC_FIELD" };

#endif	/* POINTDATA_H */
