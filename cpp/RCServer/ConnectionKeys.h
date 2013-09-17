/* 
 * File:   ConnectionKeys.h
 * Author: zoli
 *
 * Created on 2013. szeptember 10., 11:47
 */

#ifndef CONNECTIONKEYS_H
#define	CONNECTIONKEYS_H

namespace ConnectionKeys {
    
    const int KEY_DEV_HOST = 0;    
    const int KEY_DEV_CONTROLLER = 1;
    
    const int KEY_DEV_PURE_HOST = 2;
    const int KEY_DEV_PURE_CONTROLLER = 3;
    
    const int KEY_CONN_DISCONNECT = 0;
    const int KEY_CONN_MESSAGE = 1;
    const int KEY_CONN_VIDEO_STREAM = 2;
    
    const unsigned int DC_TIMEOUT1 = 1;
    const unsigned int DC_TIMEOUT2 = 10;
    const unsigned int DC_DELAY = 250;
    
};

#endif	/* CONNECTIONKEYS_H */
