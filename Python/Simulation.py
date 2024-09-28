import numpy as np
from numpy import linalg as LA
from itertools import combinations
from Body import Body
import Integrators
from Plotter import Plotter
import os

class Simulation():
    
    def __init__(self, N, dt, bodies, **sim_kwargs):
        self.bodies = bodies
        self.n = len(bodies)
        self.N = N
        self.dt = dt
        
        defaultKwargs = {
                        "Integrator":Integrators.symplecticEuler,
                        "G":1,
                        "variable_dt_constant":None,
                        }
        self.sim_kwargs = defaultKwargs | sim_kwargs

###################################################
# Simulation Calculations
###################################################

    def angularMomentum(self):
        summation = np.sum([body.mass * np.cross(body.position, body.velocity) for body in self.bodies], axis=0)
        return summation

    def centreOfMassCalc(self, totalMass):
        summation = np.sum([body.mass * body.position for body in self.bodies], axis=0) 
        position = (1/totalMass) * summation
        return position
    
    def calculatePotentialEnergy(self):
        """
        Finds the total potential energy of the simulation. Runs at each timestep
        """
        G = self.sim_kwargs["G"]
        body_pairs = list(combinations(self.bodies, 2))
        potential_energy = np.sum([-G * body1.mass * body2.mass / LA.norm(body1.position - body2.position) for body1, body2 in body_pairs])
        return potential_energy

    def kineticEnergies(self):
        '''Calculates the kinetic energy of the body at each timestep and returns the result as a numpy array'''
        kinetic_energy = np.sum([np.dot(body.velocity, body.velocity) * body.mass / 2 for body in self.bodies])
        return kinetic_energy


    def calculateAccelerations(self):
        """
        Compute the acceleration between n bodies in the x,y and z axes. The output will be a nx3 array.
        """
        G = self.sim_kwargs["G"]
        acceleration = np.zeros((self.n,3), dtype=float)
        for i, body in enumerate(self.bodies):
            acceleration[i,:] = np.sum([((-G * other_body.mass) / ((LA.norm(body.position - other_body.position))**3)) * (body.position - other_body.position) for other_body in self.bodies if other_body != body], axis=0)
        return acceleration
    
###################################################
# Run Model
###################################################

    def run(self):
        """
        Builds a 3 dimensional array filled with the 3 axes position data of every body for the length of the simulaiton
        """
        #Initialise variables
        bodies = self.bodies
        simulation = np.zeros((self.N, 6, self.n), dtype=float)
        totalMass = np.sum([body.mass for body in self.bodies])
        centreOfMass = np.zeros((self.N, 3), dtype=float)
        potentialEnergy = np.zeros((self.N), dtype=float)
        kineticEnergy = np.zeros((self.N), dtype=float)
        angularMomentum = np.zeros((self.N, 3), dtype=float)
        
        #Main time loop
        for t in range(0, self.N):
            accelerations = self.calculateAccelerations() 
            bodies = self.sim_kwargs["Integrator"](bodies, accelerations, self.dt, self.sim_kwargs["variable_dt_constant"])
            centreOfMass[t,:] = self.centreOfMassCalc(totalMass)
            potentialEnergy[t] = self.calculatePotentialEnergy()
            kineticEnergy[t] = self.kineticEnergies()
            angularMomentum[t,:] = self.angularMomentum()
            for p in range(0,self.n):
                simulation[t,:,p] = np.concatenate((bodies[p].position, bodies[p].velocity), axis=None)
        simulationSettings = np.array([self.N, self.dt, self.n, self.sim_kwargs["G"]])

        path = os.path.join(os.getcwd(), "Python\\Outputs")
        
        #Write data to files in Outputs folder
        np.savetxt(os.path.join(path, "simulationSettings.csv"), simulationSettings, delimiter=",")
        np.savetxt(os.path.join(path, "centreOfMass.csv"), centreOfMass, delimiter=",")
        np.savetxt(os.path.join(path, "potentialEnergy.csv"), potentialEnergy, delimiter=",")
        np.savetxt(os.path.join(path, "kineticEnergy.csv"), kineticEnergy, delimiter=",")
        np.savetxt(os.path.join(path, "angularMomentum.csv"), angularMomentum, delimiter=",")
        for i in range(self.n):
            np.savetxt(os.path.join(path, ("output"+ str(i)+ ".csv")), simulation[:,:,i], delimiter=",")

    def runFast(self, Integrator=Integrators.symplecticEuler):
        """
        A bare bones version of run(), only calculates body positions
        """
        bodies = self.bodies
        simulation = np.zeros((self.N, 6, self.n), dtype=float)
        for t in range(0, self.N):
            accelerations = self.calculateAccelerations()
            bodies = Integrator(bodies, accelerations, self.dt, self.sim_kwargs["variable_dt_constant"])
            for i, body in enumerate(bodies):
                simulation[t,:,i] = np.concatenate((body.position, body.velocity), axis=None)

        simulationSettings = np.array([self.N, self.dt, self.n, self.sim_kwargs["G"]])
        np.savetxt("Outputs\\simulationSettings.csv", simulationSettings, delimiter=",")
        for i in range(self.n):
            np.savetxt("Outputs\\output" + str(i) + ".csv", simulation[:,:,i], delimiter=",")
        
if __name__ == "__main__":
    import run