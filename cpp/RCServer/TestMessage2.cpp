/* 
 * File:   TestMessage2.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 14., 16:47
 */

#include "TestMessage2.h"

#include "StringUtils.h"

#include <sstream>

REGISTER_DEF_TYPE(TestMessage2, java.lang.Integer);

std::string TestMessage2::serialize() {
    std::ostringstream convert;
    convert << number;
    return convert.str();
}

void TestMessage2::deserialize(std::string json) {
    number = StringUtils::to_int(json.c_str());
}
