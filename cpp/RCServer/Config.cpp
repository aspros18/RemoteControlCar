/* 
 * File:   Config.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 6., 19:09
 */

#include "Config.h"
#include "StringUtils.h"
#include "FileUtils.h"

#include <iostream>
#include <fstream>
#include <stdexcept>

Config::Config(const char* file) {
    std::ifstream s(FileUtils::path(file).c_str());
    if (s.is_open()) {
        std::string line;
        while (s.good()) {
            std::getline(s, line);
            line = StringUtils::trim(line);
            if (line.empty() || line.at(0) == '#') continue;
            line = line.substr(0, line.find('#'));
            line = StringUtils::trim(line);
            if (!line.empty()) {
                int i = line.find(' ');
                std::string param = line.substr(0, i);
                if (param == line) continue;
                std::string value = line.substr(i, line.length());
                value = StringUtils::trim(value);
                StrPair pair(param, value);
                values.insert(pair);
            }
        }
        s.close();
    }
}

bool Config::isCorrect() {
    return getPort() != -1 && getCaFile() != "" && getCertFile() != "" && getKeyFile() != "";
}

int Config::getPort() {
    std::string val = values["port"];
    try {
        int port = StringUtils::to_int(val.c_str());
        if (port < 1 || port > 65535) return -1;
        return port;
    }
    catch (std::invalid_argument &ex) {
        return -1;
    }
}

std::string Config::getCaFile() {
    return getFile("ca");
}

std::string Config::getCertFile() {
    return getFile("crt");
}

std::string Config::getKeyFile() {
    return getFile("key");
}

std::string Config::getFile(std::string key) {
    std::string val = values[key];
    std::string pvl = FileUtils::path(val);
    if (!FileUtils::fexists(pvl)) return "";
    return pvl;
}

std::string Config::getPassword() {
    std::string val = values["password"];
    return val;
}

bool Config::isStrict() {
    std::string val = values["strict"];
    return StringUtils::to_bool(val);
}

int Config::getTimeout() {
    std::string val = values["timeout"];
    try {
        int port = StringUtils::to_int(val.c_str());
        if (port < 1) port = 1;
        if (port > 120) port = 120;
        return port;
    }
    catch (std::invalid_argument &ex) {
        return 10;
    }
}
