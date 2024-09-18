import numpy as np 
    
def symplecticEuler(bodies, accelerations, dt):
    """
    The symplectic euler numerical method, calculates the velocity at timestep n+1 using it along with the n position step to calculate the position at n+1
    """
    for (i,body) in enumerate(bodies):
        body.velocity += dt * accelerations[i,:]   # x-velocity 
        body.position += dt * body.velocity      # x-position
    
    return bodies