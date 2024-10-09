#include <iostream>
#include <vector>
#include "Vector.cpp"
#include "Body.cpp"
using namespace std;

class Integrators {
    public: 
    
    std::vector<Body> symplecticEuler(std::vector<Body> bodies, double dt) {
        for (int i = 0; i < bodies.size(); i++) {
            bodies[i].calculateAcceleration(bodies);
        }
        for (int i = 0; i < bodies.size(); i++) {
            bodies[i].setPosition(bodies[i].position() + bodies[i].velocity.scalarMultiply(dt));
            bodies[i].setVelocity(bodies[i].velocity() + bodies[i].acceleration.scalarMultiply(dt));
        }
        return bodies;
    }

};