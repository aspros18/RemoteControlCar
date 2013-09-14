/* 
 * File:   TestMessage.h
 * Author: zoli
 *
 * Created on 2013. szeptember 14., 14:10
 */

#ifndef TESTMESSAGE_H
#define	TESTMESSAGE_H

#include "Message.h"

class TestMessage : public Message {
public:
    std::string serialize();
    void deserialize(std::string json);
private:
    REGISTER_DEC_TYPE(TestMessage);
    std::string text;
};

#endif	/* TESTMESSAGE_H */

