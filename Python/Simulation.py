import numpy as np
from numpy import linalg as LA
from itertools import combinations
from Body import Body
import Integrators
from Plotter import Plotter
import os

class Simulation():
    
    def __init__(self, N, dt, bodies, **kwargs):
        self.bodies = bodies
        self.n = len(bodies)
        self.N = N
        self.dt = dt
        
        defaultKwargs = {
                        "Integrator":Integrators.symplecticEuler,
                        "G":1,
                        "variable_dt":False,
                        "focus_on_body": None,
                        }
        self.kwargs = defaultKwargs | kwargs

###################################################
# Simulation Calculations
###################################################

    def linearMomentum(self):
        if self.kwargs["focus_on_body"] is not None:
            body = self.bodies[self.kwargs["focus_on_body"]]
            p = body.mass * body.velocity
        else:
            p = np.sum([body.mass*body.velocity for body in self.bodies], axis=0)
        return p

    def angularMomentum(self):
        if self.kwargs["focus_on_body"] is not None:
            body = self.bodies[self.kwargs["focus_on_body"]]
            L = body.mass * np.cross(body.position, body.velocity)
        else:
            L = np.sum([body.mass * np.cross(body.position, body.velocity) for body in self.bodies], axis=0)
        return L

    def centreOfMassCalc(self, totalMass):
        summation = np.sum([body.mass * body.position for body in self.bodies], axis=0) 
        position = (1/totalMass) * summation
        return position
    
    def calculatePotentialEnergy(self):
        """
        Finds the total potential energy of the simulation. Runs at each timestep
        """
        G = self.kwargs["G"]
        body_pairs = list(combinations(self.bodies, 2))
        potential_energy = np.sum([-G * body1.mass * body2.mass / LA.norm(body1.position - body2.position) for body1, body2 in body_pairs])
        return potential_energy

    def kineticEnergies(self):
        '''Calculates the kinetic energy of the body at each timestep and returns the result as a numpy array'''
        kinetic_energy = np.sum([np.dot(body.velocity, body.velocity) * body.mass / 2 for body in self.bodies])
        return kinetic_energy
    
###################################################
# Run Model
###################################################

    def run(self):
        """
        Builds a 3 dimensional array filled with the 3 axes position data of every body for the length of the simulaiton
        """
        #-------------------- Initialise variables --------------------#
        bodies = self.bodies
        simulation = np.zeros((self.N, 6, self.n), dtype=float)
        totalMass = np.sum([body.mass for body in self.bodies])
        centreOfMass = np.zeros((self.N, 3), dtype=float)
        potentialEnergy = np.zeros((self.N), dtype=float)
        kineticEnergy = np.zeros((self.N), dtype=float)
        angularMomentum = np.zeros((self.N, 3), dtype=float)
        linearMomentum = np.zeros((self.N,3), dtype=float)
        G = self.kwargs["G"]
        variable_dt = self.kwargs["variable_dt"]
        
        #-------------------- Main Time Loop --------------------#
        for t in range(0, self.N):
            bodies = self.kwargs["Integrator"](bodies, self.dt, G, variable_dt)
            centreOfMass[t,:] = self.centreOfMassCalc(totalMass)
            potentialEnergy[t] = self.calculatePotentialEnergy()
            kineticEnergy[t] = self.kineticEnergies()
            angularMomentum[t,:] = self.angularMomentum()
            linearMomentum[t,:] = self.linearMomentum()
            for p in range(0,self.n):
                simulation[t,:,p] = np.concatenate((bodies[p].position, bodies[p].velocity), axis=None)
        simulationSettings = np.array([self.N, self.dt, self.n, self.kwargs["G"]])

        path = os.path.join(os.getcwd(), "Python\\Outputs")
        
        #-------------------- Write Data to CSVs --------------------#
        np.savetxt(os.path.join(path, "simulationSettings.csv"), simulationSettings, delimiter=",")
        np.savetxt(os.path.join(path, "centreOfMass.csv"), centreOfMass, delimiter=",")
        np.savetxt(os.path.join(path, "potentialEnergy.csv"), potentialEnergy, delimiter=",")
        np.savetxt(os.path.join(path, "kineticEnergy.csv"), kineticEnergy, delimiter=",")
        np.savetxt(os.path.join(path, "angularMomentum.csv"), angularMomentum, delimiter=",")
        np.savetxt(os.path.join(path, "linearMomentum.csv"), linearMomentum, delimiter=',')
        for i in range(self.n):
            np.savetxt(os.path.join(path, ("output"+ str(i)+ ".csv")), simulation[:,:,i], delimiter=",")

    def runFast(self, Integrator=Integrators.symplecticEuler):
        """
        A bare bones version of run(), only calculates body positions
        """
        path = os.path.join(os.getcwd(), "Python\\Outputs")
        bodies = self.bodies
        simulation = np.zeros((self.N, 6, self.n), dtype=float)
        for t in range(0, self.N):
            bodies = Integrator(bodies, self.dt)
            for i, body in enumerate(bodies):
                simulation[t,:,i] = np.concatenate((body.position, body.velocity), axis=None)

        simulationSettings = np.array([self.N, self.dt, self.n, self.kwargs["G"]])
        np.savetxt(os.path.join(path, "simulationSettings.csv"), simulationSettings, delimiter=",")
        for i in range(self.n):
            np.savetxt(os.path.join(path, "output" + str(i) + ".csv"), simulation[:,:,i], delimiter=",")
        
if __name__ == "__main__":
    import run