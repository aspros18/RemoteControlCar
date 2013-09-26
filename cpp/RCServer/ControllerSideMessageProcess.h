/* 
 * File:   ControllerSideMessageProcess.h
 * Author: zoli
 *
 * Created on 2013. szeptember 14., 22:41
 */

#ifndef CONTROLLERSIDEMESSAGEPROCESS_H
#define	CONTROLLERSIDEMESSAGEPROCESS_H

#include "MessageProcess.h"
#include "ControllerStorage.h"

class ControllerSideMessageProcess : public MessageProcess {
    
    public:
        
        ControllerSideMessageProcess(SSLHandler* handler);
        
        void onMessage(Message* m);
        void onUnknownMessage(UnknownMessage* m);
        void onStart();
        
    private:
        
        ControllerStorage* storage;
        
};

#endif	/* CONTROLLERSIDEMESSAGEPROCESS_H */
