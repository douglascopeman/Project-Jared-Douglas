#include <iostream>
#include <cmath>
#include <vector>
#include "Body.h"


Body::Body(const Vector& position, const Vector& velocity, double mass) 
: position(position), velocity(velocity), mass(mass) {}

void Body::calculateAcceleration(std::vector<Body*>& bodies, double G) {
    acceleration = Vector(0, 0, 0);
    for (const Body* otherBody : bodies) {
        if (this != otherBody) {
            Vector difference = position - otherBody->position;
            acceleration = acceleration + difference.scalarMultiply(-G * otherBody->mass / difference.normCubed());
        }
    }
}

void Body::calculateAcceleration(std::vector<Body*>& bodies) {
    Body::calculateAcceleration(bodies, 1);
}

// void Body::setPosition(const Vector& newPosition) {
//     position = newPosition;
// }

// void Body::setVelocity(const Vector& newVelocity) {
//     velocity = newVelocity;
// }

// Vector Body::getPosition() const {
//     return position;
// }

// Vector Body::getVelocity() const {
//     return velocity;
// }

// Vector Body::getAcceleration() const {
//     return acceleration;
// }