/* 
 * File:   SocketException.h
 * Author: zoli
 *
 * Created on 2013. szeptember 4., 16:11
 */

#ifndef SOCKETEXCEPTION_H
#define	SOCKETEXCEPTION_H

#include <string>
#include <stdexcept>

class SocketException : public std::runtime_error {
    
  public:

    enum Cause {
        init, conn_error, conn_timeout, read, write, other
    };
      
    SocketException(const std::string& msg, Cause cause=other);
    
    std::string msg();
    Cause cause();
    
  private:
      
    Cause c;
    
};

#endif	/* SOCKETEXCEPTION_H */
