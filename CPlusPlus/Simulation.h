#ifndef SIMULATION_H
#define SIMULATION_H

#include <vector>
#include "Vector.h"
#include "Body.h"

class Simulation {
private:
    std::vector<Body> bodies;
    const int n;
    const int N;
    double dt;

public:
    Simulation(std::vector<Body> bodies, int n, int N, double dt) : bodies(bodies), n(n), N(N), dt(dt) {};
    void run();
};

#endif