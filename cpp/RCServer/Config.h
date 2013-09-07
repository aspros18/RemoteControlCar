/* 
 * File:   Config.h
 * Author: zoli
 *
 * Created on 2013. szeptember 6., 19:09
 */

#ifndef CONFIG_H
#define	CONFIG_H

#include <map>
#include <string>

typedef std::pair<std::string, std::string> StrPair;
typedef std::map<std::string, std::string> StrMap;

class Config {
    
    public:
        
        Config(const char* file);
        
        bool isCorrect();
        int getPort();
        std::string getCaFile();
        std::string getCertFile();
        std::string getKeyFile();
        std::string getPassword();
        bool isStrict();
        bool certVerifyDisabled();
        int getTimeout();
        
    private:
        
        StrMap values;
        std::string getFile(std::string key);
        
};

#endif	/* CONFIG_H */

