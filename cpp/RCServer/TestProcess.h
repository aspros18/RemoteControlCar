/* 
 * File:   TestProcess.h
 * Author: zoli
 *
 * Created on 2013. szeptember 7., 9:15
 */

#ifndef TESTPROCESS_H
#define	TESTPROCESS_H

#include "SSLProcess.h"

class TestProcess : public SSLProcess {
    
    public:
        
        TestProcess(SSLHandler* handler);
        
        void run();

};

#endif	/* TESTPROCESS_H */

