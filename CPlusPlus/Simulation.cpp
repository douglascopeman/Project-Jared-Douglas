#include <iostream>
#include <cmath>
#include <vector>
#include <fstream>
#include <string>
#include "Simulation.h"
#include "Integrators.h"
using namespace std;

void Simulation::run() {
    std::vector<std::vector<std::vector<double>>> sim(N, std::vector<std::vector<double>>(6, std::vector<double>(n)));
    for (int i = 0; i < N; i++) {
        bodies = Integrators::symplecticEuler(bodies, dt);
        for (int bodyNum = 0; bodyNum < n; bodyNum++) {
            sim[i][0][bodyNum] = bodies[bodyNum].getPosition()[0];
            sim[i][1][bodyNum] = bodies[bodyNum].getPosition()[1];
            sim[i][2][bodyNum] = bodies[bodyNum].getPosition()[2];
            sim[i][3][bodyNum] = bodies[bodyNum].getVelocity()[0];
            sim[i][4][bodyNum] = bodies[bodyNum].getVelocity()[1];
            sim[i][5][bodyNum] = bodies[bodyNum].getVelocity()[2];
        }
    }


    std::ofstream settingsFile("Outputs/simulation.Settings.csv");
    if (settingsFile.is_open()) {
        settingsFile << N << "," << dt << "," << n << ",1\n";
        settingsFile.close();
    } else {
        std::cerr << "Unable to open file";
    }

    for (int bodyNum=0; bodyNum < n; bodyNum++) {
        std::ofstream bodyFile("Outputs/output" + std::to_string(bodyNum) + ".csv");
        if (bodyFile.is_open()) {
            for (int i = 0; i < N; i++) {
                bodyFile << sim[i][0][bodyNum] << "," << sim[i][1][bodyNum] << "," << sim[i][2][bodyNum] << "," << sim[i][3][bodyNum] << "," << sim[i][4][bodyNum] << "," << sim[i][5][bodyNum] << "\n";
            }
            bodyFile.close();
        } else {
            std::cerr << "Unable to open file";
        }
    }
}