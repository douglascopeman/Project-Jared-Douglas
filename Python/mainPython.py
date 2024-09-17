import numpy as np
from numpy import linalg as LA
from Body import Body

G = 1

# class Body:
#     position = np.zeros(3, dtype=float)
#     velocity = np.zeros(3, dtype=float)
#     mass = 1000

earth = Body()
moon =  Body(np.array([0,10,0], dtype=float), np.array([10,0,0], dtype=float))
# moon.position = [0, 10, 0]
# moon.velocity[0] = 10

bodies = [earth, moon]

###################################################
# Numerical Methods
###################################################
def symplecticEuler(bodies, acceleration, dt):
    """
    The symplectic euler numerical method, calculates the velocity at timestep n+1 using it along with the n position step
    to calculate the position at n+1
    """
    for (i,body) in enumerate(bodies):
        body.velocity += dt * acceleration[i,:]   # x-velocity 
        body.position += dt * body.velocity     # x-position
    
    return bodies


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


def runSimulation(bodies, T, dt):
    """
    Builds a 3 dimensional array filled with the 3 axes position data of every body for the length of the simulaiton
    """
    n = len(bodies)
    simulation = np.zeros((T, 6, n))
    # modelHamiltonian = np.zeros(simLength)
    for i in range(0, T):
        acceleration = calculateAcceleration(bodies)
        bodies = symplecticEuler(bodies, acceleration, dt)
        for p in range(0,n):
            simulation[i,:,p] = np.concatenate((bodies[p].position, bodies[p].velocity))
    
    return simulation

###################################################
# Run Model
###################################################

T = 1000    # Number of "frames" in simulation
dt = 10             # Timestep between each frame

model = runSimulation(bodies, T, dt)

if __name__ == "__main__":
    print(model)
        
        