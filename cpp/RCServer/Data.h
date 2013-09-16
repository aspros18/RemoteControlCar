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
        
        Data(D* data) {
            if (data) update(data);
        }
        
        virtual void update(D* data) = 0;
        
};

template<class D>
class PartialData : public Data<D> {
    
    public:
        
        virtual void apply(D* data) = 0;
        
        void update(D* data) {
            apply(data);
        }
    
};

#endif	/* DATA_H */
