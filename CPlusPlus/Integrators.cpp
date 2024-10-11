#include <iostream>
#include <vector>
#include "Body.h"
#include "Integrators.h"
using namespace std;

std::vector<Body> Integrators::symplecticEuler(std::vector<Body> bodies, double dt) {
    for (int i = 0; i < bodies.size(); i++) {
        bodies[i].calculateAcceleration(bodies);
    }
    for (int i = 0; i < bodies.size(); i++) {
        bodies[i].setVelocity(bodies[i].getVelocity() + bodies[i].getAcceleration().scalarMultiply(dt));
        bodies[i].setPosition(bodies[i].getPosition() + bodies[i].getVelocity().scalarMultiply(dt));
    }
    return bodies;
}