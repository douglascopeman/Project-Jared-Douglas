#ifndef BODY_H
#define BODY_H

#include <vector>
#include "Vector.h"

class Body{
private:
    double mass = 1;
    Vector position = Vector(0.0,0.0,0.0);
    Vector velocity = Vector(0.0,0.0,0.0);
    Vector acceleration = Vector(0.0,0.0,0.0);

public:
    Body(const Vector& position, const Vector& velocity, double mass) ;
    void calculateAcceleration(std::vector<Body*>& bodies, double G);
    void calculateAcceleration(std::vector<Body*>& bodies);
    inline void setPosition(const Vector& newPosition) { position = newPosition; };
    inline void setVelocity(const Vector& newVelocity) { velocity = newVelocity; };
    inline Vector getPosition() const { return position; };
    inline void updatePosition(const Vector& positionChange) { position = position + positionChange; };
    inline Vector getVelocity() const { return velocity; };
    inline void updateVelocity(const Vector& velocityChange) { velocity = velocity + velocityChange; };
    inline Vector getAcceleration() const { return acceleration; };
};

#endif
