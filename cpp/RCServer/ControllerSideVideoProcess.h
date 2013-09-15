/* 
 * File:   ControllerSideVideoProcess.h
 * Author: zoli
 *
 * Created on 2013. szeptember 15., 9:38
 */

#ifndef CONTROLLERSIDEVIDEOPROCESS_H
#define	CONTROLLERSIDEVIDEOPROCESS_H

#include "SSLProcess.h"

class ControllerSideVideoProcess : public SSLProcess {
public:
    ControllerSideVideoProcess(SSLHandler* handler);
    void run();
};

#endif	/* CONTROLLERSIDEVIDEOPROCESS_H */

