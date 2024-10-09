#ifndef BODY_H
#define BODY_H

#include <vector>
#include "Vector.h"

class Body{
private:
    float mass = 1;
    Vector position;
    Vector velocity;
    Vector acceleration;

public:
    Body(Vector position = Vector(0.0,0.0,0.0), Vector velocity = Vector(0.0,0.0,0.0), double mass);
    Body(Vector position = Vector(0.0,0.0,0.0), Vector velocity = Vector(0.0,0.0,0.0));
    void calculateAcceleration(std::vector<Body> bodies, double G);
    void calculateAcceleration(std::vector<Body> bodies);
};

#endif
