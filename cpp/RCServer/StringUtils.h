/* 
 * File:   StringUtils.h
 * Author: zoli
 *
 * Created on 2013. szeptember 6., 19:38
 */

#ifndef STRINGUTILS_H
#define	STRINGUTILS_H

#include <locale>

namespace StringUtils {

    // trim from start
    std::string &ltrim(std::string &s);

    // trim from end
    std::string &rtrim(std::string &s);

    // trim from both ends
    std::string &trim(std::string &s);

    bool to_bool(std::string &s);
    
    int to_int(char const *s);
    
}

#endif	/* STRINGUTILS_H */
