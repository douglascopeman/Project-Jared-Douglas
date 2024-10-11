#include <iostream>
#include <vector>
#include "Simulation.h"

int main() {
    // Instantiate two Body objects
    Body body1(Vector(1, 0, 0), Vector(0, 0.5, 0), 1);
    Body body2(Vector(-1, 0, 0), Vector(0, -0.5, 0), 1);
    std::vector<Body> bodies = {body1, body2};

    // Create a Simulation object
    Simulation simulation(bodies, 10, 0.1);

    simulation.run();

    return 0;
}