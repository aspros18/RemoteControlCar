/* 
 * File:   SocketJpegListener.h
 * Author: zoli
 *
 * Created on 2013. szeptember 13., 12:53
 */

#ifndef SOCKETJPEGLISTENER_H
#define	SOCKETJPEGLISTENER_H

#include "JpegListener.h"
#include "Socket.h"

#include <string>

class SocketJpegListener : public JpegListener {
    
    public:
        
        SocketJpegListener(Socket* cs, std::string key="");
        void onChanged(std::string data, bool frame);
        
    private:
        
        Socket* s;
        bool headerWrited;
        
};

#endif	/* SOCKETJPEGLISTENER_H */
