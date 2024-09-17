import numpy as np
from numpy import linalg as LA

G = 1

class Body:
    pos = np.zeros(3, dtype=float)
    vel = np.zeros(3, dtype=float)
    mass = 1000

earth = Body()
moon =  Body()
moon.pos = [0, 10, 0]
moon.vel[0] = 10

spaceData = [earth, moon]

###################################################
# Numerical Methods
###################################################
def symplecticEuler(spaceData, acc, dt):
    """
    The symplectic euler numerical method, calculates the velocity at timestep n+1 using it along with the n position step
    to calculate the position at n+1
    """
    for (i,p) in enumerate(spaceData):
        p.vel += dt * acc[i,:]   # x-velocity 
        p.pos += dt * p.vel      # x-position
    
    return spaceData




def accelerationCalc(spaceData):
    """
    Compute the acceleration between n bodies in the x,y and z axes. The output will be a nx3 array.
    """

    n = len(spaceData)
    acceleration = np.zeros((n,3), dtype=float)
    for body in range(0,n):
        acceleration[body,:] = np.sum([
            -G*spaceData[i].mass*(spaceData[body].pos - spaceData[i].pos)/((LA.norm(spaceData[body].pos - spaceData[i].pos))**3) for i in (set(range(0,n)))-set([body])])
    
    return acceleration


def runSimulation(spaceData, simLength, dt):
    """
    Builds a 3 dimensional array filled with the 3 axes position data of every body for the length of the simulaiton
    """
    nBodies = len(spaceData)
    simulation = np.zeros((simLength, 6, nBodies))
    # modelHamiltonian = np.zeros(simLength)
    for i in range(0, simLength):
        acc = accelerationCalc(spaceData)
        spaceData = symplecticEuler(spaceData, acc, dt)
        for p in range(0,nBodies):
            simulation[i,:,p] = np.concatenate((spaceData[p].pos, spaceData[p].vel))
    
    return simulation

simLength = 1000
dt = 10

model = runSimulation(spaceData, simLength, dt)
print(model)
        
        