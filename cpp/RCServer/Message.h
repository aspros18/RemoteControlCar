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

template<typename T> Message* createT() {
    return new T();
}

struct MessageFactory {
        
    typedef std::map<std::string, Message*(*)()> msg_map;

    public:
        
        static Message* createInstance(std::string const& s) {
            msg_map::iterator it = getMap()->find(s);
            if (it == getMap()->end()) return NULL;
            return it->second();
        }
        
    protected:
        
        static msg_map* getMap() {
            return &map;
        }

    private:
        
        static msg_map map;
        
};

template<typename T>
struct MessageRegister : MessageFactory {
    
    MessageRegister(std::string const& s) {
        getMap()->insert(std::make_pair(s, &createT<T>));
    }
    
};

#define REGISTER_DEC_TYPE(C_NAME) \
    static MessageRegister<C_NAME> reg

#define REGISTER_DEF_TYPE(C_NAME, J_NAME) \
    MessageRegister<C_NAME> C_NAME::reg(#J_NAME)

#endif	/* MESSAGE_H */
