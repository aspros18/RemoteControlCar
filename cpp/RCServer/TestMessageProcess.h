/* 
 * File:   TestMessageProcess.h
 * Author: zoli
 *
 * Created on 2013. szeptember 14., 22:41
 */

#ifndef TESTMESSAGEPROCESS_H
#define	TESTMESSAGEPROCESS_H

#include "MessageProcess.h"

class TestMessageProcess : public MessageProcess {
public:
    TestMessageProcess(SSLHandler* handler);
    void onMessage(Message* m);
    void onStart();
};

#endif	/* TESTMESSAGEPROCESS_H */

