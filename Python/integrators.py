import numpy as np 
    
def symplecticEuler(bodies, acceleration, dt,):
    """
    The symplectic euler numerical method, calculates the velocity at timestep n+1 using it along with the n position step to calculate the position at n+1
    """
    for (i, body) in enumerate(bodies):
        body.velocity += dt * acceleration[i, :] #body.acceleration   # x-velocity 
        body.position += dt * body.velocity      # x-position
    
    return bodies