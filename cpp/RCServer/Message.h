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

#include "rapidjson/document.h"
#include "rapidjson/writer.h"
#include "rapidjson/stringbuffer.h"
#include "StringUtils.h"

class Message {
    
    public:
        
        typedef rapidjson::Document Document;
        typedef rapidjson::Value Value;
        typedef rapidjson::SizeType SizeType;
        
        typedef rapidjson::StringBuffer Buffer;
        typedef rapidjson::Writer<Buffer> Writer;
        
        virtual void serialize(Writer& w) = 0;
        virtual void deserialize(Document& d) = 0;
        
        virtual std::string getClassName() = 0;
        
};

class UnknownMessage : public Message {
    
    public:
        
        UnknownMessage() {}
        
        UnknownMessage(std::string name, std::string def) {
            className = StringUtils::trim(name);
            definition = StringUtils::trim(def);
        }
        
        void serialize(Writer& w) {}
        void deserialize(Document& d) {}
        
        virtual std::string getClassName() {
            return className;
        }
        
        virtual std::string getDefinition() {
            return definition;
        }
        
    private:
        
        std::string className, definition;
        
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
        
        static std::string getInstanceName(Message* msg) {
            std::string s;
            for (msg_map::iterator i = getMap()->begin(); i != getMap()->end(); i++) {
                Message* tmp = i->second();
                if (tmp->getClassName() == msg->getClassName()) {
                    s = i->first;
                    delete tmp;
                    break;
                }
                delete tmp;
            }
            return s;
        }
        
    protected:
        
        static msg_map* getMap() {
            if (!map) map = new msg_map;
            return map;
        }

    private:
        
        static msg_map* map;
        
};

template<typename T>
struct MessageRegister : MessageFactory {
    
    MessageRegister(std::string const& s) {
        getMap()->insert(std::make_pair(s, &createT<T>));
    }
    
};

#define REGISTER_DEC_TYPE(C_NAME) \
    static MessageRegister<C_NAME> reg; \
    std::string getClassName() { return #C_NAME; }

#define REGISTER_DEF_TYPE(C_NAME, J_NAME) \
    MessageRegister<C_NAME> C_NAME::reg(#J_NAME)

#endif	/* MESSAGE_H */
