#include <iostream>
#include <vector>
#include <chrono>
#include <string>
#include "Simulation.h"

int main(int argc, char* argv[]) {

    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " <N> <dt>" << std::endl;
        return 1;
    }

    int N = std::stoi(argv[1]);
    double dt = std::stod(argv[2]);

    // Instantiate two Body objects
    Body body1(Vector(1, 0, 0), Vector(0, 0.5, 0), 1);
    Body body2(Vector(-1, 0, 0), Vector(0, -0.5, 0), 1);
    std::vector<Body*> bodies = {&body1, &body2}; // Vector of body pointers

    Simulation simulation(bodies, N, dt);
    
    auto start = std::chrono::high_resolution_clock::now();
    simulation.run();
    auto end = std::chrono::high_resolution_clock::now();

    std::cout << "\tTime: " << std::chrono::duration_cast<std::chrono::milliseconds>(end - start).count() << "ms" << std::endl;

    return 0;
}