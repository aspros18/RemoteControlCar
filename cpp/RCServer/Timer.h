/* 
 * File:   Timer.h
 * Author: zoli
 *
 * Created on 2013. szeptember 8., 23:51
 */

#ifndef TIMER_H
#define	TIMER_H

#include <pthread.h>

class Timer {
    
    public:
        
        Timer(long delay, long period);
        
        bool start();
        void stop();
        virtual void tick() = 0;
        
    private:
        
        bool running;
        long delay, period;
        static pthread_mutex_t mutexStart;
        
        static void* run(void*);
        
};

#endif	/* TIMER_H */
