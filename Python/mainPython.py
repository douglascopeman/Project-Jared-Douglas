import numpy as np
from numpy import linalg as LA
from Body import Body
import Integrators
from Plotter import Plotter

G = 1

earth = Body(np.array([0,0,0], dtype=float), np.array([0,0,0], dtype=float), 100)
moon =  Body(np.array([0,5,0], dtype=float), np.array([5,0,0], dtype=float))

bodies = [earth, moon]

###################################################
# Simulation Calculations
###################################################

def calculateAcceleration(bodies):
    """
    Compute the acceleration between n bodies in the x,y and z axes. The output will be a nx3 array.
    """

    n = len(bodies)
    acceleration = np.zeros((n,3), dtype=float)
    for body in range(0,n):
        acceleration[body,:] = np.sum([
            ((-G*bodies[i].mass)/((LA.norm(bodies[body].position - bodies[i].position))**3))*(bodies[body].position - bodies[i].position) for i in (set(range(0,n)))-set([body])], axis = 0)
    
    return acceleration

def centreOfMassCalc(bodies, totalMass):
    summation = np.sum([bodies[b].mass*bodies[b].position for b in range(0,n)], axis = 0)
    position = (1/totalMass)*summation
    return position



def runSimulation(bodies, T, dt, Integrator=Integrators.symplecticEuler):
    """
    Builds a 3 dimensional array filled with the 3 axes position data of every body for the length of the simulaiton
    """
    n = len(bodies)
    simulation = np.zeros((T, 6, n))
    centreOfMass = np.zeros((T,3), dtype = float)
    totalMass = np.sum([bodies[i].mass for i in range(0,n)])
    # modelHamiltonian = np.zeros(simLength)
    for i in range(0, T):
        acceleration = calculateAcceleration(bodies)
        bodies = Integrator(bodies, acceleration, dt)
        for p in range(0,n):
            simulation[i,:,p] = np.concatenate((bodies[p].position, bodies[p].velocity), axis=None)
        centreOfMass[i,:] = centreOfMassCalc(bodies, totalMass)
    
    return simulation, centreOfMass

###################################################
# Run Model
###################################################
if __name__ == "__main__":
    T = 400    # Number of "frames" in simulation
    dt = 1    # Timestep between each frame
    n = len(bodies)

    simulationSettings = np.array([T, dt, n])
    simulation, centreOfMass = runSimulation(bodies, T, dt)
    np.savetxt("Outputs\\simulationSettings.csv", simulationSettings, delimiter=",")
    for i in range(len(bodies)):
        np.savetxt("Outputs\\output" + str(i) + ".csv", simulation[:,:,i], delimiter=",")
    np.savetxt("Outputs\\centreOfMass.csv", centreOfMass, delimiter=",")


###################################################
# Plotting
###################################################

plot = Plotter("Outputs\\")

