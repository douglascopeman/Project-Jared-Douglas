import numpy as np 

class Integrators:
    
    def symplecticEuler(spaceData, acc, dt):
        """
        The symplectic euler numerical method, calculates the velocity at timestep n+1 using it along with the n position step to calculate the position at n+1
        """
        for (i,p) in enumerate(spaceData):
            p.vel += dt * acc[i,:]   # x-velocity 
            p.pos += dt * p.vel      # x-position
    
        return spaceData