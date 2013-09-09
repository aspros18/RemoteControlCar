/* 
 * File:   SSLSocketException.h
 * Author: zoli
 *
 * Created on 2013. szeptember 4., 16:16
 */

#ifndef SSLSOCKETEXCEPTION_H
#define	SSLSOCKETEXCEPTION_H

#include "SocketException.h"

class SSLSocketException : public SocketException {

  public:

    SSLSocketException(const std::string& msg, Cause cause=other);

};

#endif	/* SSLSOCKETEXCEPTION_H */
