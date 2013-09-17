/* 
 * File:   Data.h
 * Author: zoli
 *
 * Created on 2013. szeptember 16., 18:27
 */

#ifndef DATA_H
#define	DATA_H

#include "Message.h"

template<class D>
class Data : public Message {
    
    public:
        
        Data() {
            ;
        }
        
        virtual void update(D* data) = 0;
        
};

template<class D, class T>
class PartialData : public Data<D> {
    
    public:
        
        PartialData(T dat) {
            data = dat;
        }
        
        virtual void apply(D* data) = 0;
        
        void update(D* data) {
            apply(data);
        }
    
    protected:
        
        T data;
        
};

#endif	/* DATA_H */
