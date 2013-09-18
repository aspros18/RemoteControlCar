/* 
 * File:   Command.h
 * Author: zoli
 *
 * Created on 2013. szeptember 18., 19:11
 */

#ifndef COMMAND_H
#define	COMMAND_H

#include "Message.h"

class Command : public UnknownMessage {
    
    public:
        
        Command(std::string command) {
            Command::def = "\"" + command + "\"";
        }
        
        Command(UnknownMessage* msg) {
            if (Command::isCommand(msg)) {
                Command::def = msg->getDefinition();
            }
        }
        
        static bool isCommand(UnknownMessage* msg) {
            return msg->getClassName() == Command::name;
        }
        
        std::string getClassName() {
            return Command::name;
        }
        
        std::string getDefinition() {
            return def;
        }
        
        std::string getCommand() {
            std::string def = getDefinition();
            if (def.empty()) return def;
            return def.substr(1, def.size() - 2);
        }
        
    private:
        
        std::string def;
        static std::string name;
        
};

std::string Command::name = "org.dyndns.fzoli.rccar.model.Command";

#endif	/* COMMAND_H */