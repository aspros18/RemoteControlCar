/* 
 * File:   TestMessage2.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 14., 16:47
 */

#include "TestMessage2.h"

REGISTER_DEF_TYPE(TestMessage2, "java.lang.String");

std::string TestMessage2::serialize() {
    return "\"" + text + "\"";
}

void TestMessage2::deserialize(std::string json) {
    text = json.substr(1, json.length() - 2);
}
