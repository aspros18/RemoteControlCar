/* 
 * File:   Message.h
 * Author: zoli
 *
 * Created on 2013. szeptember 13., 23:53
 */

#ifndef MESSAGE_H
#define	MESSAGE_H

#include <string>
#include <map>

class Message {
    
    public:
        
        virtual std::string serialize() = 0;
        virtual void deserialize(std::string json) = 0;
        
};

#endif	/* MESSAGE_H */
