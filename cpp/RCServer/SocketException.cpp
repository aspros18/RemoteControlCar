/* 
 * File:   SocketException.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 4., 16:11
 */

#include "SocketException.h"

SocketException::SocketException(const std::string& msg, Cause cause) : std::runtime_error(msg) {
    c = cause;
}

std::string SocketException::msg() {
  std::string desc(what());
  return desc;
}

SocketException::Cause SocketException::cause() {
    return c;
}
