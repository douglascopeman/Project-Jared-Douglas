#include <iostream>
#include "Vector.cpp"
using namespace std;

class Body{
private:
    float mass = 1;
    // double position[3];
    // double velocity[3];
    // double acceleration[3];
    Vector position;
    Vector velocity;
    Vector acceleration;
    float G = 1;

public:

    //Default Constructor
    Body(Vector position, Vector velocity, double mass) {
        this->position = position;
        this->velocity = velocity;
        this->mass = mass;
    }

    double calculateAcceleration(Body bodies[]) {
        Vector acceleration = Vector(0, 0, 0);
        for (int i = 0; i < sizeof(bodies); i++) {
            if (&bodies[i] != this) {
                Vector positionDifference = bodies[i].position - position;
                acceleration = acceleration + positionDifference.scalarMultiply(-G * bodies[i].mass / pow(positionDifference.norm(), 3));
            }
        }
    }
};