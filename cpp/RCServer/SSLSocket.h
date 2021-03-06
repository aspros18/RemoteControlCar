/* 
 * File:   SSLSocket.h
 * Author: zoli
 *
 * Created on 2013. szeptember 2., 19:58
 */

#ifndef SSLSOCKET_H
#define	SSLSOCKET_H

#include <openssl/ssl.h>

#include <pthread.h>

#include "Socket.h"

class SSLSocket : public Socket {
    
    protected:
        
        struct  connection {
            int socket;
            SSL *sslHandle;
        };
        
    public:
        
        SSLSocket(connection c);
        SSLSocket(const char *host, uint16_t port, const char *CAfile, const char *CRTfile, const char *KEYfile, void *passwd, int timeout = 0, bool verify = true);
        virtual ~SSLSocket();
        
        char* getClientName();
        char* getServerName();
        void close();
        int write(const void *buf, int num) const;
        int read(void *buf, int num) const;
        
        using Socket::write;
        using Socket::read;
        
    private:
        
        bool reading;
        connection conn;
        char *clientName, *serverName;
        static int count;
        static pthread_mutex_t mutexCount;
        pthread_mutex_t mutexClose;
        
        static void loadSSL();
        static void unloadSSL();
        static char *getCommonName(X509 *cert);
        int sslConnect(const char *addr, uint16_t port, int timeout);
        
    protected:
        
        SSL_CTX* ctx;
        
        SSLSocket();
        
        static SSL_CTX *sslCreateCtx(bool client, bool verify, const char *CAfile, const char *CRTfile, const char *KEYfile, void *passwd);
        static void sslDestroyCtx(SSL_CTX *sctx);
        static void sslDisconnect(SSLSocket* s, connection c);
        
};

#endif	/* SSLSOCKET_H */
