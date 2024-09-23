import numpy as np
from numpy import linalg as LA
from itertools import combinations
from Body import Body
import Integrators
from Plotter import Plotter

class Simulation():
    
    def __init__(self, T, dt, bodies, G=1):
        self.G = G
        self.bodies = bodies
        self.n = len(bodies)
        self.T = T
        self.dt = dt


###################################################
# Run Model
###################################################
    # def run(self):
    #     simulationSettings = np.array([self.T, self.dt, self.n])
    #     simulation, centreOfMass, potentialEnergy = self.runTwo(self.bodies)
    #     np.savetxt("Outputs\\simulationSettings.csv", simulationSettings, delimiter=",")
    #     np.savetxt("Outputs\\centreOfMass.csv", centreOfMass, delimiter=",")
    #     np.savetxt("Outputs\\potentialEnergy.csv", potentialEnergy, delimiter=",")
    #     for i in range(self.n):
    #         np.savetxt("Outputs\\output" + str(i) + ".csv", simulation[:,:,i], delimiter=",")


###################################################
# Simulation Calculations
###################################################

    def centreOfMassCalc(self, totalMass):
        summation = np.sum([self.bodies[b].mass*self.bodies[b].position for b in range(0,self.n)], axis = 0)
        position = (1/totalMass)*summation
        return position
    
    def calculatePotentialEnergy(self):
        """
        Finds the total potential energy of the simulation. Runs at each timestep
        """
        # Number of bodies choose 2
        combinationList = list(combinations(range(0,self.n), 2))
        potentialEnergy = np.sum([
            -self.G * self.bodies[combinationList[i][0]].mass * self.bodies[combinationList[i][1]].mass 
            / (LA.norm(self.bodies[combinationList[i][0]].position - self.bodies[combinationList[i][1]].position)) 
            for i in range(0,len(combinationList))])
        return potentialEnergy

    def kinetic_energies(self):
        '''Calculates the kinetic energy of the body at each timestep and returns the result as a numpy array'''
        kinetic_energy = np.sum([np.dot(body.velocity, body.velocity) * body.mass / 2 for body in self.bodies])
        return kinetic_energy


    def calculateAccelerations(self):
        """
        Compute the acceleration between n bodies in the x,y and z axes. The output will be a nx3 array.
        """
        acceleration = np.zeros((self.n,3), dtype=float)
        for body in range(0,self.n):
            acceleration[body,:] = np.sum([
                ((-self.G * self.bodies[i].mass)/((LA.norm(self.bodies[body].position - self.bodies[i].position))**3))*(self.bodies[body].position - self.bodies[i].position) for i in (set(range(0,self.n)))-set([body])], axis = 0)
    
        return acceleration
    
###################################################
# Run Model
###################################################

    def run(self, Integrator=Integrators.symplecticEuler):
        """
        Builds a 3 dimensional array filled with the 3 axes position data of every body for the length of the simulaiton
        """
        bodies = self.bodies
        simulation = np.zeros((self.T, 6, self.n), dtype=float)
        totalMass = np.sum([self.bodies[i].mass for i in range(1, self.n)])
        centreOfMass = np.zeros((self.T, 3), dtype=float)
        potentialEnergy = np.zeros((self.T), dtype=float)
        kineticEnergy = np.zeros((self.T), dtype=float)
        for i in range(0, self.T):
            accelerations = self.calculateAccelerations()
            bodies = Integrator(bodies, accelerations, self.dt)
            centreOfMass[i,:] = self.centreOfMassCalc(totalMass)
            potentialEnergy[i] = self.calculatePotentialEnergy()
            kineticEnergy[i] = self.kinetic_energies()
            for p in range(0,self.n):
                simulation[i,:,p] = np.concatenate((bodies[p].position, bodies[p].velocity), axis=None)

        simulationSettings = np.array([self.T, self.dt, self.n])
        np.savetxt("Outputs\\simulationSettings.csv", simulationSettings, delimiter=",")
        np.savetxt("Outputs\\centreOfMass.csv", centreOfMass, delimiter=",")
        np.savetxt("Outputs\\potentialEnergy.csv", potentialEnergy, delimiter=",")
        np.savetxt("Outputs\\kineticEnergy.csv", kineticEnergy, delimiter=",")
        for i in range(self.n):
            np.savetxt("Outputs\\output" + str(i) + ".csv", simulation[:,:,i], delimiter=",")

    def runFast(self, Integrator=Integrators.symplecticEuler):
        """
        A bare bons version of run(), only calculates body positions
        """
        bodies = self.bodies
        simulation = np.zeros((self.T, 6, self.n), dtype=float)
        for i in range(0, self.T):
            accelerations = self.calculateAccelerations()
            bodies = Integrator(bodies, accelerations, self.dt)
            for p in range(0,self.n):
                simulation[i,:,p] = np.concatenate((bodies[p].position, bodies[p].velocity), axis=None)

        simulationSettings = np.array([self.T, self.dt, self.n])
        np.savetxt("Outputs\\simulationSettings.csv", simulationSettings, delimiter=",")
        for i in range(self.n):
            np.savetxt("Outputs\\output" + str(i) + ".csv", simulation[:,:,i], delimiter=",")


