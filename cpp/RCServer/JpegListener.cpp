/* 
 * File:   JpegListener.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 13., 13:02
 */

#include "JpegListener.h"

JpegListener::JpegListener(std::string key) {
    this->key = key;
}
        
std::string JpegListener::getKey() {
    return key;
}
