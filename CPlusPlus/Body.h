#ifndef BODY_H
#define BODY_H

#include <vector>
#include "Vector.h"

class Body{
private:
    float mass = 1;
    Vector position = Vector(0.0,0.0,0.0);
    Vector velocity = Vector(0.0,0.0,0.0);
    Vector acceleration = Vector(0.0,0.0,0.0);

public:
    Body(Vector position, Vector velocity, float mass);
    void calculateAcceleration(std::vector<Body*>& bodies, float G);
    void calculateAcceleration(std::vector<Body*>& bodies);
    void setPosition(const Vector& position);
    void setVelocity(const Vector& velocity);
    Vector getPosition() const;
    Vector getVelocity() const;
    Vector getAcceleration() const;
};

#endif
