import numpy as np
from numpy import linalg as LA
from Body import Body
import Integrators

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
    for body in bodies:
        other_bodies = list(set(bodies)-set([body]))
        body.determine_acceleration(other_bodies)


    # n = len(bodies)
    # acceleration = np.zeros((n,3), dtype=float)
    # for body in range(0,n):
    #     acceleration[body,:] = np.sum([
    #         -G*bodies[i].mass*(bodies[body].position - bodies[i].position)/((LA.norm(bodies[body].position - bodies[i].position))**3) for i in (set(range(0,n)))-set([body])])
    
    # return acceleration


def runSimulation(bodies, T, dt, Integrator=Integrators.symplecticEuler):
    """
    Builds a 3 dimensional array filled with the 3 axes position data of every body for the length of the simulaiton
    """
    n = len(bodies)
    simulation = np.zeros((T, 6, n))
    # modelHamiltonian = np.zeros(simLength)
    for i in range(0, T):
        calculateAccelerations(bodies)
        bodies = Integrator(bodies, dt)
        for p in range(0,n):
            simulation[i,:,p] = np.concatenate((bodies[p].position, bodies[p].velocity))
    
    return simulation


        
        