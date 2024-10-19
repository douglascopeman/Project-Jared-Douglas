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
        self.focus_body = None
        
        defaultKwargs = {
                        "Integrator":Integrators.symplecticEuler,
                        "G":1,
                        "is_variable_dt":False,
                        "is_focus_on_body": False,
                        "stop_conditions": None
                        }
        self.kwargs = defaultKwargs | kwargs

###################################################
# Simulation Calculations
###################################################


    def linearMomentum(self):
        p = [body.mass*body.velocity for body in self.bodies]
        p_norm = [LA.norm(q) for q in p]

        if self.kwargs["is_focus_on_body"]:
            if self.focus_body is not None:
                return p[self.focus_body]
            else:
                self.focus_body = p_norm.index(np.max(p_norm))
                return p[self.focus_body]
        else:
            return np.sum(p, axis=0)

    def angularMomentum(self):
        L = [body.mass * np.cross(body.position, body.velocity) for body in self.bodies]
        L_norm = [LA.norm(q) for q in L]
        if self.kwargs["is_focus_on_body"]:
            if self.focus_body is not None:
                return L[self.focus_body]
            else:
                self.focus_body = L_norm.index(np.max(L_norm))
                return L[self.focus_body]
        else:
            return np.sum(L, axis=0)

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
        stop_conditions = self.kwargs["stop_conditions"]
        bodies = self.bodies
        simulation = np.concatenate((bodies[p].position, bodies[p].velocity), axis=None)
        totalMass = np.sum([body.mass for body in self.bodies])
        centreOfMass = self.centreOfMassCalc(totalMass)
        potentialEnergy = self.calculatePotentialEnergy()
        kineticEnergy = self.kineticEnergies()
        angularMomentum = self.angularMomentum()
        linearMomentum = self.linearMomentum()
        G = self.kwargs["G"]
        is_variable_dt = self.kwargs["is_variable_dt"]
        
        # Holding Initial Values for Error
        initialPotentialEnergy = np.copy(potentialEnergy)
        initialKineticEnergy = np.copy(kineticEnergy)
        initialAngularMomentum = np.copy(angularMomentum)
        initialLinearMomentum = np.copy(linearMomentum)
        
        #-------------------- Main Time Loop --------------------#
        for t in range(0, self.N):
            if stop_conditions is not None:
                
                break
            bodies = self.kwargs["Integrator"](bodies, self.dt, G, is_variable_dt)
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
    import Testing