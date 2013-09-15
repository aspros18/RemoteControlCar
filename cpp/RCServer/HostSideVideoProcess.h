/* 
 * File:   HostSideVideoProcess.h
 * Author: zoli
 *
 * Created on 2013. szeptember 15., 9:37
 */

#ifndef HOSTSIDEVIDEOPROCESS_H
#define	HOSTSIDEVIDEOPROCESS_H

#include "SSLProcess.h"

class HostSideVideoProcess : public SSLProcess {
public:
    HostSideVideoProcess(SSLHandler* handler);
    void run();
};

#endif	/* HOSTSIDEVIDEOPROCESS_H */

