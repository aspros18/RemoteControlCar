/* 
 * File:   TestDisconnectProcess.h
 * Author: zoli
 *
 * Created on 2013. szeptember 14., 22:50
 */

#ifndef TESTDISCONNECTPROCESS_H
#define	TESTDISCONNECTPROCESS_H

#include "DisconnectProcess.h"


class TestDisconnectProcess : public DisconnectProcess {
    
public:
    
    TestDisconnectProcess(SSLHandler* handler);
    
    void onConnect();
    void afterAnswer();
    void afterTimeout();
    void onTimeout(std::exception* ex);
    void onDisconnect(std::exception* ex);
    
};

#endif	/* TESTDISCONNECTPROCESS_H */

