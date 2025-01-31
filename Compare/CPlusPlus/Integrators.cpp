#include <iostream>
#include <vector>
#include "Body.h"
#include "Integrators.h"

std::vector<Body*> Integrators::symplecticEuler(std::vector<Body*>& bodies, double dt) {
    for (Body* body : bodies) {
        body->calculateAcceleration(bodies);
    }
    for (Body* body : bodies) {
        const Vector& acceleration = body->getAcceleration();
        Vector velocityUpdate = acceleration.scalarMultiply(dt);
        body->updateVelocity(velocityUpdate);

        const Vector& velocity = body->getVelocity();
        Vector positionUpdate = velocity.scalarMultiply(dt);
        body->updatePosition(positionUpdate);
    }
    return bodies;
}