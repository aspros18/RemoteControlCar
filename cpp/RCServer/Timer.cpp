/* 
 * File:   Timer.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 8., 23:51
 */

#include "Timer.h"

#include <unistd.h>

pthread_mutex_t Timer::mutexStart = PTHREAD_MUTEX_INITIALIZER;

Timer::Timer(unsigned int delay, unsigned int period, bool runOnce) : mutexDestroy(PTHREAD_MUTEX_INITIALIZER) {
    this->delay = delay;
    this->period = period;
    this->runOnce = runOnce;
    this->running = false;
}

Timer::~Timer() {
    stop();
    pthread_mutex_lock(&mutexDestroy);
    pthread_mutex_unlock(&mutexDestroy);
}

bool Timer::start() {
    pthread_mutex_lock(&mutexStart);
    if (running) {
        pthread_mutex_unlock(&mutexStart);
        return true;
    }
    pthread_t pt;
    if (!pthread_create(&pt, NULL,  run, this)) {
        running = true;
        pthread_mutex_unlock(&mutexStart);
        return true;
    }
    pthread_mutex_unlock(&mutexStart);
    return false;
}

void Timer::stop() {
    running = false;
}

void* Timer::run(void* v) {
    Timer* t = (Timer*) v;
    pthread_mutex_lock(&t->mutexDestroy);
    try {
        sleep(t->delay);
        while (t->running) {
            t->tick();
            if (t->runOnce) break;
            sleep(t->period);
        }
    }
    catch (...) {
        t->stop();
    }
    pthread_mutex_unlock(&t->mutexDestroy);
    pthread_exit(NULL);
}
