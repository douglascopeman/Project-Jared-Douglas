#include <iostream>
#include <vector>
#include "Body.h"
#include "Integrators.h"

std::vector<Body*> Integrators::symplecticEuler(std::vector<Body*>& bodies, float dt) {
    for (Body* body : bodies) {
        body->calculateAcceleration(bodies);
    }
    for (Body* body : bodies) {
        body->setVelocity(body->getVelocity() + body->getAcceleration().scalarMultiply(dt));
        body->setPosition(body->getPosition() + body->getVelocity().scalarMultiply(dt));
    }
    return bodies;
}