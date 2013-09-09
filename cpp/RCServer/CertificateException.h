/* 
 * File:   CertificateException.h
 * Author: zoli
 *
 * Created on 2013. szeptember 4., 16:45
 */

#ifndef CERTIFICATEEXCEPTION_H
#define	CERTIFICATEEXCEPTION_H

#include "SSLSocketException.h"

class CertificateException : public SSLSocketException {

  public:

    enum Cert {
        ca, crt, key, other
    };
    
    CertificateException(const std::string& msg, Cert cert=other);

};

#endif	/* CERTIFICATEEXCEPTION_H */
