/* 
 * File:   TestMessage2.h
 * Author: zoli
 *
 * Created on 2013. szeptember 14., 16:47
 */

#ifndef TESTMESSAGE2_H
#define	TESTMESSAGE2_H

#include "Message.h"
#include "MessageFactory.h"

class TestMessage2 : public Message {
public:
    std::string serialize();
    void deserialize(std::string json);
private:
    REGISTER_DEC_TYPE(TestMessage2);
    std::string text;
};

#endif	/* TESTMESSAGE2_H */

