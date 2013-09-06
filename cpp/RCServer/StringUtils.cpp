/* 
 * File:   StringUtils.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 6., 19:38
 */

#include "StringUtils.h"

#include <algorithm>
#include <functional>
#include <cctype>

#include <strings.h>
#include <cstdlib>
#include <iostream>

#include <stdexcept>

std::string &StringUtils::ltrim(std::string &s) {
    s.erase(s.begin(), std::find_if(s.begin(), s.end(), std::not1(std::ptr_fun<int, int>(std::isspace))));
    return s;
}

std::string &StringUtils::rtrim(std::string &s) {
    s.erase(std::find_if(s.rbegin(), s.rend(), std::not1(std::ptr_fun<int, int>(std::isspace))).base(), s.end());
    return s;
}

std::string &StringUtils::trim(std::string &s) {
    return ltrim(rtrim(s));
}

bool StringUtils::to_bool (std::string &v) {
    return !v.empty () &&
        (strcasecmp (v.c_str (), "true") == 0 ||
         atoi (v.c_str ()) != 0);
}

int StringUtils::to_int(char const *s) {
    if ( s == NULL || s[0] == '\0' ) {
        throw  std::invalid_argument("null or empty string argument");
    }
    bool negate = (s[0] == '-');
    if ( *s == '+' || *s == '-' ) ++s;
    int result = 0;
    while(*s) {
        if ( *s >= '0' && *s <= '9' ) {
            result = result * 10  - (*s - '0');  //assume negative number
        }
        else {
            throw std::invalid_argument("invalid input string");
        }
        ++s;
    }
    return negate ? result : -result; //-result is positive!
}
