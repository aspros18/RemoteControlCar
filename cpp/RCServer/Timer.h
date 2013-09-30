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
        
        Timer(unsigned int delay, unsigned int period, bool runOnce=false);
        virtual ~Timer();
        
        bool start();
        void stop();
        virtual void tick() = 0;
        
    private:
        
        bool runOnce, running;
        unsigned int delay, period;
        pthread_mutex_t mutexDestroy;
        static pthread_mutex_t mutexStart;
        
        static void* run(void*);
        
};

#endif	/* TIMER_H */
