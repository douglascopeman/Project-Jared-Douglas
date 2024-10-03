import numpy as np 
import Simulation
from numpy import linalg as LA
import copy

def get_variable_dt_helper(bodies, variable_dt_constant):
    """
    Calculates the desired timestep at the current frame
    """
    # Initialising the possible timesteps
    possible_dts = np.zeros(len(bodies), dtype=float)

    # Each possible timestep equal to the constant times one ove rthe acceleration of the body
    for (i, body) in enumerate(bodies):
        possible_dts[i] = variable_dt_constant * 1 / np.linalg.norm(body.velocity)

    return min(possible_dts) # We pick the smallest of the timesteps 

def get_variable_dt(bodies, variable_dt_constant):

    temp_dt = get_variable_dt_helper(bodies, variable_dt_constant)  # We find the temporary timestep moving forwards
    temp_bodies = copy.deepcopy(symplecticEuler(bodies, temp_dt))                  # We find the temporary state moving forwards
    for body in temp_bodies:
        body.velocity = -1*body.velocity                  # Reversing state direction

    temp_dt_backwards = get_variable_dt_helper(temp_bodies, variable_dt_constant)
    average_dt = (temp_dt+temp_dt_backwards)/2
    return average_dt

    
def symplecticEuler(bodies, dt, G=1, variable_dt_constant=None):
    """
    The symplectic euler numerical method, calculates the velocity at timestep n+1 using it along with the n position step to calculate the position at n+1
    """
    # Finding the acceleration of each body
    for body in bodies:
            body.calculate_acceleration(bodies)

    if variable_dt_constant is not None:
        dt = get_variable_dt(bodies, variable_dt_constant)
    
    for body in bodies:
        body.velocity += dt * body.acceleration
        body.position += dt * body.velocity 
        
    return bodies

def symplecticEulerHalfSteps(bodies, dt, G=1, variable_dt_constant=None):
    for body in bodies:
        body.calculate_acceleration(bodies)
        
    if variable_dt_constant is not None:
        for body in bodies:
            dt = variable_dt_constant * np.linalg.norm(body.position) / np.linalg.norm(body.velocity)
            body.velocity += dt/2 * body.acceleration
            dt = variable_dt_constant * np.linalg.norm(body.position) / np.linalg.norm(body.velocity)
            body.position += dt * body.velocity
            dt = variable_dt_constant * np.linalg.norm(body.position) / np.linalg.norm(body.velocity)
        for body in bodies:
            body.calculate_acceleration(bodies)
        for body in bodies:
            body.velocity += dt/2 * body.acceleration
    else:
        body.velocity += dt * body.acceleration
        body.position += dt * body.velocity
        
    return bodies
            

def Euler(bodies, dt, G=1, variable_dt_constant=None):
    """
    The symplectic euler numerical method, calculates the velocity at timestep n+1 using it along with the n position step to calculate the position at n+1
    """
    for body in bodies:
        body.calculate_acceleration(bodies)
    
    for (i, body) in enumerate(bodies):
        body.position += dt * body.velocity  
        body.velocity += dt * body.acceleration
            
    return bodies

# def calculateAccelerations(bodies, G):
#     acceleration = np.zeros((len(bodies),3), dtype=float)
#     for i, body in enumerate(bodies):
#         acceleration[i,:] = np.sum([((-G * other_body.mass) / ((LA.norm(body.position - other_body.position))**3)) * (body.position - other_body.position) for other_body in bodies if other_body != body], axis=0)
#         return acceleration


# changed to use body class's acceleration calculation. Still needs to be tested
def threeStepLeapFrog(bodies, dt, G, variable_dt_constant=None):
    """
    The 3-Step Leapfrog method in "kick-drift-kick" form is both symplectic and can take a variable timestep
    """
    for body in bodies:
        body.calculate_acceleration(bodies)

    halfVelocity = np.zeros((len(bodies), 3), dtype=float)
    for (i, body) in enumerate(bodies):
        halfVelocity[i, :] = body.velocity + body.acceleration*dt/2
        body.position += halfVelocity[i,:]*dt
    
    # acceleration = calculateAccelerations(bodies, G)
    for body in bodies:
        body.calculate_acceleration(bodies)

    for (i, body)  in enumerate(bodies):
        body.velocity = halfVelocity[i,:] + body.acceleration * dt/2

    return bodies
