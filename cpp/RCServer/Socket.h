/* 
 * File:   Socket.h
 * Author: zoli
 *
 * Created on 2013. szeptember 5., 18:55
 */

#ifndef SOCKET_H
#define	SOCKET_H

#include <stdint.h>
#include <string>

class Socket {
    
    public:
        
        Socket(const char *host, uint16_t port, int timeout = 0);
        
        Socket(int sock);
        
        bool isClosed();
        std::streambuf* getBuffer();
        virtual void close() = 0; // TODO: meg van írva, csak azért abstract, mert nem mindig jó az override; utána kellene járni, miért
        void write(int byte);
        int write(const char *text) const;
        virtual int write(const void *buf, int num) const = 0; // TODO: ez is meg van írva...
        int read();
        void read (std::string& s) const;
        virtual int read(void *buf, int num) const = 0; // TODO: ez is meg van írva...
        virtual void setTimeout(int sec);
        
        const Socket& operator >> (std::string& s) const;
        const Socket& operator << (const std::string& s) const;
        
    protected:
        
        bool closed;
        int m_sock;
        std::streambuf* buffer;
        
        Socket();
        
        static int tcpConnect(const char *addr, uint16_t port, int timeout);
        static void setTimeout(int socket, int sec);
        
};

#endif	/* SOCKET_H */
