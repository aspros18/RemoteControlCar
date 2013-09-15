/* 
 * File:   JpegListener.h
 * Author: zoli
 *
 * Created on 2013. szeptember 13., 13:02
 */

#ifndef JPEGLISTENER_H
#define	JPEGLISTENER_H

#include <string>

class JpegListener {
    public:
        JpegListener(std::string key="");
        virtual bool onChanged(std::string data, bool frame) = 0;
        virtual std::string getKey();
    private:
        std::string key;
};

#endif	/* JPEGLISTENER_H */
