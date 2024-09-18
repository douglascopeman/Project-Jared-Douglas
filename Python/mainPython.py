import numpy as np
from numpy import linalg as LA
from Body import Body
import Integrators

G = 1

earth = Body()
moon =  Body(np.array([0,10,0], dtype=float), np.array([10,0,0], dtype=float), 1000)

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
            -G*bodies[i].mass*(bodies[body].position - bodies[i].position)/((LA.norm(bodies[body].position - bodies[i].position))**3) for i in (set(range(0,n)))-set([body])])
    
    return acceleration


def runSimulation(bodies, T, dt, Integrator=Integrators.symplecticEuler):
    """
    Builds a 3 dimensional array filled with the 3 axes position data of every body for the length of the simulaiton
    """
    n = len(bodies)
    simulation = np.zeros((T, 6, n))
    # modelHamiltonian = np.zeros(simLength)
    for i in range(0, T):
        acceleration = calculateAcceleration(bodies)
        bodies = Integrator(bodies, acceleration, dt)
        for p in range(0,n):
            simulation[i,:,p] = np.concatenate((bodies[p].position, bodies[p].velocity))
    
    return simulation

###################################################
# Run Model
###################################################
if __name__ == "__main__":
    T = 1000    # Number of "frames" in simulation
    dt = 10     # Timestep between each frame

    model = runSimulation(bodies, T, dt)
    print(model)
    for i in range(len(bodies)):
        np.savetxt("Outputs\\output" + str(i) + ".csv", model[:,:,i], delimiter=",")
        
        