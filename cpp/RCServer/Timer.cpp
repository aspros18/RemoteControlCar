/* 
 * File:   Timer.cpp
 * Author: zoli
 * 
 * Created on 2013. szeptember 8., 23:51
 */

#include "Timer.h"

#include <unistd.h>

pthread_mutex_t Timer::mutexStart = PTHREAD_MUTEX_INITIALIZER;

Timer::Timer(unsigned int delay, unsigned int period, bool runOnce) {
    this->delay = delay;
    this->period = period;
    this->runOnce = runOnce;
    this->running = false;
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
    sleep(t->delay);
    while (t->running) {
        t->tick();
        if (t->runOnce) break;
        sleep(t->period);
    }
    pthread_exit(NULL);
}
