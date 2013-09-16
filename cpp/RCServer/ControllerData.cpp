/* 
 * File:   TestData.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 16., 19:30
 */

#include "ControllerData.h"

REGISTER_DEF_TYPE(ControllerData, org.dyndns.fzoli.rccar.model.controller.ControllerData);

std::string ControllerData::serialize() {
    return "{\"hostName\":\"host\",\"hostState\":{\"AZIMUTH\":105},\"hostUnderTimeout\":false,\"vehicleConnected\":false,\"controlling\":false,\"wantControl\":false,\"viewOnly\":false,\"connected\":true,\"CHAT_MESSAGES\":[],\"CONTROLLERS\":[{\"NAME\":\"controller\",\"lastModified\":\"Sep 15, 2013 10:21:50 AM\",\"controlling\":false,\"wantControl\":false}],\"fullX\":false,\"fullY\":false,\"up2date\":false,\"control\":{\"mX\":0,\"mY\":0}}";
}

void ControllerData::deserialize(std::string json) {
    
}

void ControllerData::update(ControllerData* data) {
    
}
