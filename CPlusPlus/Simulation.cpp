#include <iostream>
#include <cmath>
#include <vector>
#include "Vector.h"
#include "Body.h"
#include "Simulation.h"
#include "Integrators.h"
using namespace std;

Simulation::Simulation(std::vector<Body> bodies, int n, int N, double dt) : bodies(bodies), n(n), N(N), dt(dt) {}

void Simulation::run() {
    std::vector<std::vector<std::vector<double>>> sim(N, std::vector<std::vector<double>>(6, std::vector<double>(n)));
    for (int i = 0; i < N; i++) {
        bodies = Integrators::symplecticEuler(bodies, dt);

    }
}