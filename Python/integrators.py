import numpy as np 
import Simulation
from numpy import linalg as LA

    
def symplecticEuler(bodies, acceleration, dt, G=1, variable_dt_constant=None):
    """
    The symplectic euler numerical method, calculates the velocity at timestep n+1 using it along with the n position step to calculate the position at n+1
    """
    
    if variable_dt_constant is None:
        for (i, body) in enumerate(bodies):
            body.velocity += dt * acceleration[i, :]
            body.position += dt * body.velocity  
    else:
        for (i, body) in enumerate(bodies):
            temp_dt_one = variable_dt_constant * np.linalg.norm(body.position) / np.linalg.norm(body.velocity)
            temp_vel = body.velocity + temp_dt_one * acceleration[i, :]
            temp_pos = body.position + temp_dt_one * body.velocity   
            temp_dt_two = variable_dt_constant * np.linalg.norm(temp_pos) / np.linalg.norm(temp_vel)
            dt_avg = (temp_dt_one + temp_dt_two) / 2
            body.velocity = body.velocity + dt_avg * acceleration[i, :]
            body.position = body.position + dt_avg * body.velocity
               
    
    return bodies

def Euler(bodies, acceleration, dt, G=1, variable_dt_constant=None):
    """
    The symplectic euler numerical method, calculates the velocity at timestep n+1 using it along with the n position step to calculate the position at n+1
    """
    for (i, body) in enumerate(bodies):
        body.position += dt * body.velocity  
        body.velocity += dt * acceleration[i, :]
            
    return bodies

def calculateAccelerations(bodies, G):
    acceleration = np.zeros((len(bodies),3), dtype=float)
    for i, body in enumerate(bodies):
        acceleration[i,:] = np.sum([((-G * other_body.mass) / ((LA.norm(body.position - other_body.position))**3)) * (body.position - other_body.position) for other_body in bodies if other_body != body], axis=0)
        return acceleration

def ThreeStepLeapFrog(bodies, acceleration, dt, G, variable_dt_constant=None):
    """
    The 3-Step Leapfrog method in "kick-drift-kick" form is both symplectic and can take a variable timestep
    """
    halfVelocity = np.zeros((len(bodies), 3), dtype=float)
    for (i, body) in enumerate(bodies):
        halfVelocity[i, :] = body.velocity + acceleration[i,:]*dt/2
        body.position += halfVelocity[i,:]*dt
    
    acceleration = calculateAccelerations(bodies, G)

    for (i, body)  in enumerate(bodies):
        body.velocity = halfVelocity[i,:] + acceleration[i,:] * dt/2

    return bodies
