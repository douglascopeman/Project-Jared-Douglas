#ifndef SIMULATION_H
#define SIMULATION_H

#include <vector>
#include "Body.h"

class Simulation {
private:
    std::vector<Body*> bodies;
    const int n;
    const int N;
    double dt;

public:
    Simulation(std::vector<Body*> bodies, int N, float dt) : bodies(bodies), n(bodies.size()), N(N), dt(dt) {};
    void run();
};

#endif