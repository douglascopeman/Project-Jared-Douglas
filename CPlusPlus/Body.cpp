#include <iostream>
#include <cmath>
#include <vector>
#include "Body.h"
using namespace std;


Body::Body(Vector position, Vector velocity, double mass) 
: position(position), velocity(velocity), mass(mass) {}

void Body::calculateAcceleration(std::vector<Body*> bodies, double G) {
    acceleration = Vector(0, 0, 0);
    for (Body* otherBody : bodies) {
        if (this != otherBody) {
            Vector difference = position - (*otherBody).position;
            acceleration = acceleration + difference.scalarMultiply(-G * (*otherBody).mass / pow(difference.norm(), 3));
        }
    }
}

void Body::calculateAcceleration(std::vector<Body*> bodies) {
    Body::calculateAcceleration(bodies, 1);
}

void Body::setPosition(Vector newPosition) {
    position = newPosition;
}

void Body::setVelocity(Vector newVelocity) {
    velocity = newVelocity;
}

Vector Body::getPosition() {
    return position;
}

Vector Body::getVelocity() {
    return velocity;
}

Vector Body::getAcceleration() {
    return acceleration;
}