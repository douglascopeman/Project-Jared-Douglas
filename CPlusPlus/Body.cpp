#include <iostream>
#include <vector>
#include "Body.h"
using namespace std;


/**
 * @brief Constructs a new Body object with the given position, velocity, and mass.
 * 
 * @param position The initial position of the body.
 * @param velocity The initial velocity of the body.
 * @param mass The mass of the body.
 */
Body::Body(Vector position, Vector velocity, double mass) 
: position(position), velocity(velocity), mass(mass) {}

// Body::Body(Vector position, Vector velocity) : position(position), velocity(velocity), mass(1) {}

void Body::calculateAcceleration(std::vector<Body> bodies, double G) {
    for (Body otherBody : bodies) {
        if (this != &otherBody) {
            Vector direction = position - otherBody.position;
            direction.print();
            double r_norm = direction.norm();
            double r_norm_cubed = r_norm * r_norm * r_norm;
            acceleration = acceleration + direction.scalarMultiply(-G * otherBody.mass / r_norm_cubed);
        }
    }
}

void Body::calculateAcceleration(std::vector<Body> bodies) {
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