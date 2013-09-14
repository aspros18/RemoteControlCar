/* 
 * File:   TestMessage.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 14., 14:10
 */

#include "TestMessage.h"

REGISTER_DEF_TYPE(TestMessage, java.lang.String);

TestMessage::TestMessage() {
    ;
}

TestMessage::TestMessage(std::string s) {
    text = s;
}

std::string TestMessage::serialize() {
    return "\"" + text + "\"";
}

void TestMessage::deserialize(std::string json) {
    text = json.substr(1, json.length() - 2);
}
