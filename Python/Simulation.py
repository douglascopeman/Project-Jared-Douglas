import numpy as np
from numpy import linalg as LA
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
    def run(self):
        simulationSettings = np.array([self.T, self.dt, self.n])
        simulation, centreOfMass = self.runTwo(self.bodies)
        np.savetxt("Outputs\\simulationSettings.csv", simulationSettings, delimiter=",")
        np.savetxt("Outputs\\centreOfMass.csv", centreOfMass, delimiter=",")
        for i in range(self.n):
            np.savetxt("Outputs\\output" + str(i) + ".csv", simulation[:,:,i], delimiter=",")


###################################################
# Simulation Calculations
###################################################

    def centreOfMassCalc(self, totalMass):
        summation = np.sum([self.bodies[b].mass*self.bodies[b].position for b in range(0,self.n)], axis = 0)
        position = (1/totalMass)*summation
        return position


    def calculateAccelerations(self):
        """
        Compute the acceleration between n bodies in the x,y and z axes. The output will be a nx3 array.
        """

        acceleration = np.zeros((self.n,3), dtype=float)
        for body in range(0,self.n):
            acceleration[body,:] = np.sum([
                ((-self.G * self.bodies[i].mass)/((LA.norm(self.bodies[body].position - self.bodies[i].position))**3))*(self.bodies[body].position - self.bodies[i].position) for i in (set(range(0,self.n)))-set([body])], axis = 0)
    
        return acceleration

    def runTwo(self, bodies, Integrator=Integrators.symplecticEuler):
        """
        Builds a 3 dimensional array filled with the 3 axes position data of every body for the length of the simulaiton
        """
        simulation = np.zeros((self.T, 6, self.n), dtype=float)
        totalMass = np.sum([self.bodies[i].mass for i in range(1, self.n)])
        centreOfMass = np.zeros((self.T, 3), dtype=float)
        # modelHamiltonian = np.zeros(simLength)
        for i in range(0, self.T):
            accelerations = self.calculateAccelerations()
            bodies = Integrator(bodies, accelerations, self.dt)
            centreOfMass[i,:] = self.centreOfMassCalc(totalMass)
            for p in range(0,self.n):
                simulation[i,:,p] = np.concatenate((bodies[p].position, bodies[p].velocity), axis=None)
    
        return simulation, centreOfMass
###################################################
# Run Model
###################################################
# if __name__ == "__main__":
#     T = 400    # Number of "frames" in simulation
#     dt = 1    # Timestep between each frame
#     n = len(bodies)

#     simulationSettings = np.array([T, dt, n])
#     simulation = runSimulation(bodies, T, dt)
#     np.savetxt("Outputs\\simulationSettings.csv", simulationSettings, delimiter=",")
#     for i in range(len(bodies)):
#         np.savetxt("Outputs\\output" + str(i) + ".csv", simulation[:,:,i], delimiter=",")
###################################################
# Plotting
###################################################
# if __name__ == "__main__":
#     plot = Plotter("Outputs\\")

