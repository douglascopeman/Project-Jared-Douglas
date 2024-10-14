#include <iostream>
#include <vector>
#include "Simulation.h"

int main() {
    // Instantiate two Body objects
    Body body1(Vector(1, 0, 0), Vector(0, 0.5, 0), 1);
    Body body2(Vector(-1, 0, 0), Vector(0, -0.5, 0), 1);
    std::vector<Body*> bodies = {&body1, &body2}; // Vector of body pointers

    // Create a Simulation object
    Simulation simulation(bodies, 2500, 0.01);

    simulation.run();

    return 0;
}