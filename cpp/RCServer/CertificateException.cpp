/* 
 * File:   CertificateException.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 4., 16:45
 */

#include "CertificateException.h"

CertificateException::CertificateException(const std::string& msg, Cert cert) : SSLSocketException(msg) {
    ;
}
