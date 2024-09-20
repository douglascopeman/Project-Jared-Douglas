import numpy as np 
    
def symplecticEuler(bodies, dt):
    """
    The symplectic euler numerical method, calculates the velocity at timestep n+1 using it along with the n position step to calculate the position at n+1
    """
    for body in bodies:
        body.velocity += dt * body.acceleration   # x-velocity 
        body.position += dt * body.velocity      # x-position
    
    return bodies