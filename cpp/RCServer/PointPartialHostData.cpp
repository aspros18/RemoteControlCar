/* 
 * File:   PointPartialHostData.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 18., 12:24
 */

#include "PointPartialHostData.h"

REGISTER_DEF_TYPE(PointPartialHostData, org.dyndns.fzoli.rccar.model.host.HostData$PointPartialHostData);

PointPartialHostData::PointPartialHostData() : PointPartialBaseData<HostData>() {
    ;
}

PointPartialHostData::PointPartialHostData(Point3D dat, PointType type) : PointPartialBaseData<HostData>(dat, type) {
    ;
}

void PointPartialHostData::apply(HostData* data) {
    ;
}
