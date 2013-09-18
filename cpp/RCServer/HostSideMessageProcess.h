/* 
 * File:   HostSideMessageProcess.h
 * Author: zoli
 *
 * Created on 2013. szeptember 15., 10:29
 */

#ifndef HOSTSIDEMESSAGEPROCESS_H
#define	HOSTSIDEMESSAGEPROCESS_H

#include "MessageProcess.h"

class HostSideMessageProcess : public MessageProcess {
public:
    HostSideMessageProcess(SSLHandler* handler);
    void onMessage(Message* m);
    void onUnknownMessage(UnknownMessage* m);
    void onStart();
};

#endif	/* HOSTSIDEMESSAGEPROCESS_H */
