#include <iostream>
#include <cmath>
#include <vector>
#include <fstream>
#include <string>
#include <thread>
#include "Simulation.h"
#include "Integrators.h"
using namespace std;

void WriteBodyData(const std::vector<std::vector<std::vector<double>>>& sim, int N, int bodyNum) {
        std::ofstream bodyFile("Outputs/output" + std::to_string(bodyNum) + ".csv");
        if (!bodyFile.is_open()) {
            std::cerr << "Unable to open file for body " << bodyNum << "\n";
            return;
        }

        for (int i = 0; i < N; i++) {
            bodyFile << sim[i][0][bodyNum] << "," << sim[i][1][bodyNum] << "," << sim[i][2][bodyNum] << ","
                     << sim[i][3][bodyNum] << "," << sim[i][4][bodyNum] << "," << sim[i][5][bodyNum] << "\n";
        }

        bodyFile.close();
    }

void WriteData(const std::vector<std::vector<std::vector<double>>>& sim, int N, int dt, int n) {
    std::ofstream settingsFile("Outputs/simulationSettings.csv");
    if (settingsFile.is_open()) {
        settingsFile << N << "," << dt << "," << n << ",1\n";
        settingsFile.close();
    } else {
        std::cerr << "Unable to open file";
        return;
    }

    std::vector<std::thread> threads;
    for (int bodyNum = 0; bodyNum < n; bodyNum++) {
        threads.emplace_back(WriteBodyData, std::ref(sim), N, bodyNum);
    }

    for (auto& thread : threads) {
        thread.join();
    }
    
}

void Simulation::run() {

    std::vector<std::vector<std::vector<double>>> sim(N, std::vector<std::vector<double>>(6, std::vector<double>(n)));
    for (int i = 0; i < N; i++) {
        bodies = Integrators::symplecticEuler(bodies, dt);
        for (int bodyNum = 0; bodyNum < n; bodyNum++) {
            const Vector& pos = bodies[bodyNum]->getPosition();
            const Vector& vel = bodies[bodyNum]->getVelocity();
            sim[i][0][bodyNum] = pos[0];
            sim[i][1][bodyNum] = pos[1];
            sim[i][2][bodyNum] = pos[2];
            sim[i][3][bodyNum] = vel[0];
            sim[i][4][bodyNum] = vel[1];
            sim[i][5][bodyNum] = vel[2];
        }
    }

    WriteData(sim, N, dt, n);
}