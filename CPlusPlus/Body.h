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
    Body(Vector position, Vector velocity, double mass);
    void calculateAcceleration(std::vector<Body*> bodies, double G);
    void calculateAcceleration(std::vector<Body*> bodies);
    void setPosition(Vector position);
    void setVelocity(Vector velocity);
    Vector getPosition();
    Vector getVelocity();
    Vector getAcceleration();
};

#endif
