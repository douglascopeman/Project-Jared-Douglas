import numpy as np
from numpy import linalg as LA
from Body import Body
import integrators
from Plotter import Plotter

earth = Body(np.array([-5,5,0], dtype=float), np.array([-0.5,0,0], dtype=float), 10)
moon =  Body(np.array([5,-5,0], dtype=float), np.array([0.5,0,0], dtype=float), 10)
bodies = [earth, moon]
T = 10    # Number of "frames" in simulation
dt = 0.1    # Timestep between each frame

G= 1

###################################################
# Run Model
###################################################
def run(T, dt, bodies, G=1):
    
    n = len(bodies)

    simulationSettings = np.array([T, dt, n])
    simulation = runSimulation(bodies, T, dt)
    np.savetxt("Outputs\\simulationSettings.csv", simulationSettings, delimiter=",")
    for i in range(len(bodies)):
        np.savetxt("Outputs\\output" + str(i) + ".csv", simulation[:,:,i], delimiter=",")

###################################################
# Simulation Calculations
###################################################

def calculateAccelerations(bodies):
    """
    Compute the acceleration between n bodies in the x,y and z axes. The output will be a nx3 array.
    """

    n = len(bodies)
    acceleration = np.zeros((n,3), dtype=float)
    for body in range(0,n):
        acceleration[body,:] = np.sum([
            ((-G*bodies[i].mass)/((LA.norm(bodies[body].position - bodies[i].position))**3))*(bodies[body].position - bodies[i].position) for i in (set(range(0,n)))-set([body])], axis = 0)
    
    return acceleration



def runSimulation(bodies, T, dt, Integrator=integrators.symplecticEuler):
    """
    Builds a 3 dimensional array filled with the 3 axes position data of every body for the length of the simulaiton
    """
    n = len(bodies)
    simulation = np.zeros((T, 6, n))
    # modelHamiltonian = np.zeros(simLength)
    for i in range(0, T):
        accelerations = calculateAccelerations(bodies)
        bodies = Integrator(bodies, accelerations, dt)
        for p in range(0,n):
            simulation[i,:,p] = np.concatenate((bodies[p].position, bodies[p].velocity), axis=None)
    
    return simulation

###################################################
# Run Model
###################################################
if __name__ == "__main__":
    T = 400    # Number of "frames" in simulation
    dt = 1    # Timestep between each frame
    n = len(bodies)

    simulationSettings = np.array([T, dt, n])
    simulation = runSimulation(bodies, T, dt)
    np.savetxt("Outputs\\simulationSettings.csv", simulationSettings, delimiter=",")
    for i in range(len(bodies)):
        np.savetxt("Outputs\\output" + str(i) + ".csv", simulation[:,:,i], delimiter=",")


###################################################
# Plotting
###################################################

plot = Plotter("Outputs\\")

